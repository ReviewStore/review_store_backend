package com.retro.domain.retro.application;

import com.retro.domain.member.domain.MemberRepository;
import com.retro.domain.member.domain.entity.Member;
import com.retro.domain.retro.application.dto.request.RetroCreateRequest;
import com.retro.domain.retro.domain.entity.InterviewQuestion;
import com.retro.domain.retro.domain.entity.Retro;
import com.retro.domain.retro.domain.repository.RetroRepository;
import com.retro.global.common.exception.BusinessException;
import com.retro.global.common.exception.ErrorCode;
import java.util.List;
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

  @Transactional
  public Retro createRetro(Long memberId, RetroCreateRequest request) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

    // 1. DTO를 통해 애그리거트 루트(Retro) 생성
    Retro retro = request.toEntity(member);

    // 2. 하위 질문 DTO들을 엔티티로 변환하여 루트에 추가
    if (isNotEmptyQuestions(request)) {
      List<InterviewQuestion> questionEntities = request.toQuestionEntities();
      retro.addQuestions(questionEntities);
    }

    // 3. 루트 저장 (Cascade로 InterviewQuestion도 함께 저장)
    retroRepository.save(retro);
    return retro;
  }

  private boolean isNotEmptyQuestions(RetroCreateRequest request) {
    return !CollectionUtils.isEmpty(request.questions());
  }


}
