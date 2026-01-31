package com.retro.domain.retro.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.retro.domain.member.application.MemberFacade;
import com.retro.domain.member.domain.MemberRepository;
import com.retro.domain.member.domain.entity.Member;
import com.retro.domain.retro.application.dto.request.QuestionRequest;
import com.retro.domain.retro.application.dto.request.RetroCreateRequest;
import com.retro.domain.retro.application.dto.response.KeywordResponse;
import com.retro.domain.retro.application.dto.response.RetroDetailResponse;
import com.retro.domain.retro.domain.entity.InterviewQuestion;
import com.retro.domain.retro.domain.entity.Keyword;
import com.retro.domain.retro.domain.entity.Retro;
import com.retro.domain.retro.domain.repository.KeywordRepository;
import com.retro.domain.retro.domain.repository.RetroRepository;
import com.retro.global.common.exception.BusinessException;
import com.retro.global.common.exception.ErrorCode;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RetroServiceTest {

  @InjectMocks
  private RetroService retroService;

  @Mock
  private RetroRepository retroRepository;

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private KeywordRepository keywordRepository;

  @Mock
  private MemberFacade memberFacade;

  @Nested
  @DisplayName("회고 생성(createRetro) 테스트")
  class CreateRetro {

    @Test
    @DisplayName("성공: 질문이 포함된 회고를 생성하면 연관관계가 설정된 상태로 저장된다.")
    void successWithQuestions() {
      // given
      Long memberId = 1L;
      Member member = mock(Member.class);
      RetroCreateRequest request = mock(RetroCreateRequest.class);

      List<QuestionRequest> questionRequests = List.of(
          QuestionRequest.of(1, "기술", "JVM이란?", "답변", "좋음", 5)
      );

      Retro retro = Retro.of(member, "카카오", "백엔드", LocalDate.now(), "1차", "#Java", "K", "P", "T",
          "요약");

      List<InterviewQuestion> questions = List.of(
          InterviewQuestion.of(1, "기술", "JVM이란?", "답변", "상", 3)
      );

      given(memberFacade.getMember(memberId)).willReturn(member);
      given(request.toEntity(member)).willReturn(retro);
      given(request.questions()).willReturn(questionRequests);
      given(request.toQuestionEntities()).willReturn(questions);
      given(retroRepository.save(any(Retro.class))).willReturn(retro);
      given(member.hasLimitedPermissionAndOpenedOwnPublication()).willReturn(true);

      // when
      Retro result = retroService.createRetro(memberId, request);

      // then
      assertThat(result).isNotNull();
      assertThat(result.getQuestions()).hasSize(1);
      assertThat(result.getQuestions().get(0).getRetro()).isEqualTo(result);

      verify(memberFacade).getMember(memberId);
      verify(memberFacade).grantUnlimitedPostReadPermissionToMember(member);
      verify(retroRepository).save(retro);

      // 회원 조회가 Facade로 이동했다면 repository 조회는 호출되면 안 됨
      verify(memberRepository, never()).findById(any());
    }


    @Test
    @DisplayName("성공: 질문이 없는 경우에도 회고 본문만 정상적으로 저장된다.")
    void successWithoutQuestions() {
      // given
      Long memberId = 1L;
      Member member = mock(Member.class);
      RetroCreateRequest request = mock(RetroCreateRequest.class);

      Retro retro = Retro.of(member, "네이버", "FE", LocalDate.now(), "2차", "#React", "K", "P", "T",
          "요약");

      given(memberFacade.getMember(memberId)).willReturn(member);
      given(request.toEntity(member)).willReturn(retro);
      given(request.questions()).willReturn(Collections.emptyList());
      given(member.hasLimitedPermissionAndOpenedOwnPublication()).willReturn(true);

      // when
      retroService.createRetro(memberId, request);

      // then
      assertThat(retro.getQuestions()).isEmpty();

      verify(memberFacade).getMember(memberId);
      verify(request, never()).toQuestionEntities();
      verify(retroRepository).save(retro);
      verify(memberRepository, never()).findById(any());
      verify(memberFacade).grantUnlimitedPostReadPermissionToMember(member);

    }

    @Test
    @DisplayName("성공: 조건을 만족하지 않으면 무제한 권한 부여 요청을 보내지 않는다")
    void success_withoutQuestions_and_noGrant_whenConditionFalse() {
      // given
      Long memberId = 1L;
      Member member = mock(Member.class);
      RetroCreateRequest request = mock(RetroCreateRequest.class);
      Retro retro = mock(Retro.class);

      given(memberFacade.getMember(memberId)).willReturn(member);
      given(request.toEntity(member)).willReturn(retro);
      given(request.questions()).willReturn(Collections.emptyList());
      given(member.hasLimitedPermissionAndOpenedOwnPublication()).willReturn(false);

      // when
      retroService.createRetro(memberId, request);

      // then
      verify(retroRepository).save(retro);
      verify(memberFacade, never()).grantUnlimitedPostReadPermissionToMember(any());
      verify(memberRepository, never()).findById(any());
    }


    @Test
    @DisplayName("실패: 회원이 존재하지 않으면 MEMBER_NOT_FOUND 예외를 던진다.")
    void failWhenMemberNotFound() {
      // given
      Long memberId = 1L;
      RetroCreateRequest request = mock(RetroCreateRequest.class);

      given(memberFacade.getMember(memberId))
          .willThrow(new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

      // when & then
      assertThatThrownBy(() -> retroService.createRetro(memberId, request))
          .isInstanceOf(BusinessException.class)
          .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MEMBER_NOT_FOUND);

      verify(memberFacade).getMember(memberId);
      verify(retroRepository, never()).save(any());
      verify(memberRepository, never()).findById(any());
    }


  }

  @Nested
  @DisplayName("키워드 검색(searchKeywords) 테스트")
  class SearchKeywords {

    @Test
    @DisplayName("성공: 검색어가 포함된 키워드 목록을 반환한다.")
    void successSearchKeywords() {
      // given
      String searchContent = "데이터";
      List<Keyword> mockKeywords = List.of(
          Keyword.of("데이터 분석가", "개발/데이터"),
          Keyword.of("빅데이터", "개발/데이터")
      );

      // keywordRepository가 해당 키워드 리스트를 반환하도록 모킹
      given(keywordRepository.findAllByContentContaining(searchContent))
          .willReturn(mockKeywords);

      // when
      List<KeywordResponse> result = retroService.searchKeywords(searchContent);

      // then
      assertThat(result).hasSize(2);
      assertThat(result.get(0).name()).isEqualTo("데이터 분석가");
      assertThat(result.get(0).category()).isEqualTo("개발/데이터");
      assertThat(result.get(1).name()).isEqualTo("빅데이터");

      verify(keywordRepository).findAllByContentContaining(searchContent);
    }

    @Test
    @DisplayName("성공: 검색 결과가 없는 경우 빈 리스트를 반환한다.")
    void successSearchKeywordsWithEmptyResult() {
      // given
      String searchContent = "존재하지않는키워드";
      given(keywordRepository.findAllByContentContaining(searchContent))
          .willReturn(List.of());

      // when
      List<KeywordResponse> result = retroService.searchKeywords(searchContent);

      // then
      assertThat(result).isEmpty();
      verify(keywordRepository).findAllByContentContaining(searchContent);
    }
  }


  @Nested
  @DisplayName("회고 상세 조회(getRetro)")
  class GetRetro {

    @Test
    @DisplayName("실패: 회고가 없으면 RETRO_NOT_FOUND")
    void fail_retroNotFound() {
      // given
      Long viewerId = 1L;
      Long retroId = 999L;

      Member viewer = mock(Member.class);
      given(memberFacade.getMember(viewerId)).willReturn(viewer);
      given(retroRepository.findById(retroId)).willReturn(Optional.empty());

      // when & then
      assertThatThrownBy(() -> retroService.getRetro(viewerId, retroId))
          .isInstanceOf(BusinessException.class)
          .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RETRO_NOT_FOUND);

      verify(viewer, never()).reduceRemainingPostReadCount();
    }

    @Test
    @DisplayName("성공: 본인 글이면 차감 없이 조회된다")
    void success_owner_noConsume() {
      // given
      Long viewerId = 1L;
      Long retroId = 10L;

      Member viewer = mock(Member.class);
      Retro retro = mock(Retro.class);
      Member author = mock(Member.class);

      given(memberFacade.getMember(viewerId)).willReturn(viewer);
      given(retroRepository.findById(retroId)).willReturn(Optional.of(retro));
      given(retro.getMember()).willReturn(author);
      given(author.getId()).willReturn(viewerId);

      given(retro.isCreatedByViewer(viewerId, viewerId)).willReturn(true);

      // when
      RetroDetailResponse response = retroService.getRetro(viewerId, retroId);

      // then
      assertThat(response).isNotNull();
      verify(viewer, never()).isPostReadCountExceeded();
      verify(viewer, never()).reduceRemainingPostReadCount();
    }

    @Test
    @DisplayName("실패: 타인 글 + 잔여 0이면 예외 발생, 차감하지 않는다")
    void fail_other_and_exceeded() {
      // given
      Long viewerId = 2L;
      Long retroId = 10L;

      Member viewer = mock(Member.class);
      Retro retro = mock(Retro.class);
      Member author = mock(Member.class);

      given(memberFacade.getMember(viewerId)).willReturn(viewer);
      given(retroRepository.findById(retroId)).willReturn(Optional.of(retro));
      given(retro.getMember()).willReturn(author);
      given(author.getId()).willReturn(1L);

      given(retro.isCreatedByViewer(1L, viewerId)).willReturn(false);
      given(viewer.isPostReadCountExceeded()).willReturn(true);

      // when & then
      assertThatThrownBy(() -> retroService.getRetro(viewerId, retroId))
          .isInstanceOf(BusinessException.class)
          .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RETRO_READ_POINT_EXCEEDED);

      verify(viewer, never()).reduceRemainingPostReadCount();
    }

    @Test
    @DisplayName("성공: 타인 글 + 잔여 남음이면 1회 차감 후 조회된다")
    void success_other_and_consume() {
      // given
      Long viewerId = 2L;
      Long retroId = 10L;

      Member viewer = mock(Member.class);
      Retro retro = mock(Retro.class);
      Member author = mock(Member.class);

      given(memberFacade.getMember(viewerId)).willReturn(viewer);
      given(retroRepository.findById(retroId)).willReturn(Optional.of(retro));
      given(retro.getMember()).willReturn(author);
      given(author.getId()).willReturn(1L);

      given(retro.isCreatedByViewer(1L, viewerId)).willReturn(false);
      given(viewer.isPostReadCountExceeded()).willReturn(false);

      // when
      RetroDetailResponse response = retroService.getRetro(viewerId, retroId);

      // then
      assertThat(response).isNotNull();
      verify(viewer).reduceRemainingPostReadCount();
    }
  }


}