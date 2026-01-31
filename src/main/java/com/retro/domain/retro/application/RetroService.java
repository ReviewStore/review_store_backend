package com.retro.domain.retro.application;

import com.retro.domain.member.application.MemberFacade;
import com.retro.domain.member.domain.MemberRepository;
import com.retro.domain.member.domain.entity.Member;
import com.retro.domain.retro.application.dto.request.RetroCreateRequest;
import com.retro.domain.retro.application.dto.response.KeywordResponse;
import com.retro.domain.retro.application.dto.response.RetroDetailResponse;
import com.retro.domain.retro.domain.entity.InterviewQuestion;
import com.retro.domain.retro.domain.entity.Retro;
import com.retro.domain.retro.domain.repository.KeywordRepository;
import com.retro.domain.retro.domain.repository.RetroRepository;
import com.retro.global.common.exception.BusinessException;
import com.retro.global.common.exception.ErrorCode;
import java.util.List;
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
  private final MemberRepository memberRepository;
  private final KeywordRepository keywordRepository;
  private final MemberFacade memberFacade;

  @Transactional
  public Retro createRetro(Long memberId, RetroCreateRequest request) {
    Member member = memberFacade.getMember(memberId);

    // 1. DTO를 통해 애그리거트 루트(Retro) 생성
    Retro retro = request.toEntity(member);

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

    Long authorId = retro.getMember().getId();

    if (retro.isCreatedByViewer(authorId, viewerId)) {
      return RetroDetailResponse.from(retro);
    }
    if (viewer.isPostReadCountExceeded()) {
      throw new BusinessException(ErrorCode.RETRO_READ_POINT_EXCEEDED);
    }
    viewer.reduceRemainingPostReadCount();
    return RetroDetailResponse.from(retro);
  }
}
