package com.retro.domain.retro.application;

import com.retro.domain.member.application.MemberFacade;
import com.retro.domain.member.domain.entity.Member;
import com.retro.domain.retro.application.dto.RetroCursorPageResponse;
import com.retro.domain.retro.application.dto.request.RetroCreateRequest;
import com.retro.domain.retro.application.dto.request.RetroUpdateRequest;
import com.retro.domain.retro.application.dto.response.KeywordResponse;
import com.retro.domain.retro.application.dto.response.RetroDetailResponse;
import com.retro.domain.retro.domain.entity.InterviewQuestion;
import com.retro.domain.retro.domain.entity.Retro;
import com.retro.domain.retro.domain.entity.RetroReport;
import com.retro.domain.retro.domain.event.RetroBlindedEvent;
import com.retro.domain.retro.domain.event.RetroEventPublisher;
import com.retro.domain.retro.domain.event.RetroReadLimitWarningEvent;
import com.retro.domain.retro.domain.repository.KeywordRepository;
import com.retro.domain.retro.domain.repository.RetroRepository;
import com.retro.global.common.exception.BusinessException;
import com.retro.global.common.exception.ErrorCode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class RetroService {

  private final RetroRepository retroRepository;
  private final KeywordRepository keywordRepository;
  private final MemberFacade memberFacade;
  private final RetroEventPublisher retroEventPublisher;

  @Transactional
  public Retro createRetro(Long memberId, RetroCreateRequest request) {
    Member member = memberFacade.getMember(memberId);

    // 1. DTO를 통해 애그리거트 루트(Retro) 생성
    Retro retro = request.toEntity(member.getId());

    // 2. 하위 질문 DTO들을 엔티티로 변환하여 루트에 추가
    if (isNotEmptyQuestions(request)) {
      List<InterviewQuestion> questionEntities = request.toQuestionEntities();
      retro.addQuestions(questionEntities);
    }

    // 3. 루트 저장 (Cascade로 InterviewQuestion도 함께 저장)
    retroRepository.save(retro);

    // 4. 회원의 게시물 공개 여부 조회 & 열람권 무제한 부여 여부 검증
    if (member.hasLimitedPermissionAndOpenedOwnPublication()) {
      memberFacade.grantUnlimitedPostReadPermissionToMember(member);
    }

    return retro;
  }

  private boolean isNotEmptyQuestions(RetroCreateRequest request) {
    return !CollectionUtils.isEmpty(request.questions());
  }

  public List<KeywordResponse> searchKeywords(String content) {
    return keywordRepository.findAllByContentContaining(content)
        .stream()
        .map(KeywordResponse::from)
        .collect(Collectors.toList());
  }

  @Transactional
  public RetroDetailResponse getRetro(Long viewerId, Long retroId) {
    Member viewer = memberFacade.getMember(viewerId);

    Retro retro = retroRepository.findById(retroId)
        .orElseThrow(() -> new BusinessException(ErrorCode.RETRO_NOT_FOUND));

    if (retro.isBlindedRetro()) {
      throw new BusinessException(ErrorCode.RETRO_BLINDED);
    }

    Long authorId = retro.getMemberId();

    if (retro.isCreatedByViewer(authorId, viewerId)) {
      return RetroDetailResponse.from(retro);
    }
    if (viewer.isPostReadCountExceeded()) {
      throw new BusinessException(ErrorCode.RETRO_READ_POINT_EXCEEDED);
    }
    viewer.reduceRemainingPostReadCount();
    if (viewer.hasOneRemainingPostReadCount()) {
      retroEventPublisher.publishRetroReadLimitWarningEvent(RetroReadLimitWarningEvent.of(viewer));
    }
    return RetroDetailResponse.from(retro);
  }

  public RetroCursorPageResponse getMyRetros(Long memberId, Long cursorId, int size) {
    Member member = memberFacade.getMember(memberId);
    List<Retro> retros = getMyRetros(cursorId, size, member);
    boolean hasNext = hasMoreRetros(size, retros);
    if (hasNext) {
      retros = sliceRetros(size, retros);
    }
    List<RetroDetailResponse> responses = retros.stream()
        .map(RetroDetailResponse::from)
        .toList();

    Long nextCursor = getNextCursor(hasNext, retros);

    return RetroCursorPageResponse.of(responses, nextCursor, hasNext);
  }

  private List<Retro> getMyRetros(Long cursorId, int size, Member member) {
    final int pageSizePlusOne = size + 1;
    return retroRepository.findByMemberIdWithCursor(member.getId(), cursorId,
        pageSizePlusOne);
  }

  private Long getNextCursor(boolean hasNext, List<Retro> retros) {
    return hasNext ? retros.getLast().getRetroId() : null;
  }

  private List<Retro> sliceRetros(int size, List<Retro> retros) {
    return retros.subList(0, size);
  }

  private boolean hasMoreRetros(int size, List<Retro> retros) {
    return retros.size() > size;
  }

  @Transactional
  public void updateRetrosForWithdrawnMember(Long memberId) {
    List<Retro> retros = retroRepository.findAllByMemberId(memberId);
    updateDeletedMembersRetros(retros);
  }

  private void updateDeletedMembersRetros(List<Retro> retros) {
    retros.forEach(retro -> retro.markAsWithdrawnMember(Member.DELETED_MEMBER_ID));
  }

  @Transactional
  public void reportRetro(Long reporterId, Long retroId) {

    Retro retro = retroRepository.findById(retroId)
        .orElseThrow(() -> new BusinessException(ErrorCode.RETRO_NOT_FOUND));

    Optional<RetroReport> optionalRetroReport = retroRepository.existsReportByRetroAndReporter(
        retroId, reporterId);

    if (optionalRetroReport.isPresent()) {
      throw new BusinessException(ErrorCode.RETRO_ALREADY_REPORTED);
    }

    boolean isBlinded = retro.report();

    RetroReport newReportByConnectingUser = RetroReport.of(retro, reporterId);
    retro.addReport(newReportByConnectingUser);

    if (isBlinded) {
      retroEventPublisher.publishRetroBlindedEvent(RetroBlindedEvent.of(retro));
    }
    
  }

  @Transactional
  public void updateRetro(Long memberId, Long retroId, RetroUpdateRequest request) {
    Retro retro = retroRepository.findById(retroId)
        .orElseThrow(() -> new BusinessException(ErrorCode.RETRO_NOT_FOUND));

    validateOwner(retro, memberId);

    retro.update(
        request.companyName(),
        request.position(),
        request.interviewDate(),
        request.interviewRound(),
        request.interviewTags(),
        request.keepText(),
        request.problemText(),
        request.tryText(),
        request.summary()
    );

    List<InterviewQuestion> newQuestions = request.toQuestionEntities();
    retro.replaceQuestions(newQuestions);
  }

  @Transactional
  public void deleteRetro(Long memberId, Long retroId) {
    Retro retro = retroRepository.findById(retroId)
        .orElseThrow(() -> new BusinessException(ErrorCode.RETRO_NOT_FOUND));

    validateOwner(retro, memberId);

    retroRepository.delete(retro);
  }

  private void validateOwner(Retro retro, Long memberId) {
    if (!retro.isOwnedBy(memberId)) {
      throw new BusinessException(ErrorCode.RETRO_NOT_OWNER);
    }
  }
}
