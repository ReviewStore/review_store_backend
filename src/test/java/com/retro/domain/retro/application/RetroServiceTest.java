package com.retro.domain.retro.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.retro.domain.member.domain.MemberRepository;
import com.retro.domain.member.domain.entity.Member;
import com.retro.domain.retro.application.dto.request.QuestionRequest;
import com.retro.domain.retro.application.dto.request.RetroCreateRequest;
import com.retro.domain.retro.application.dto.response.KeywordResponse;
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

      given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
      given(request.toEntity(member)).willReturn(retro);
      given(request.questions()).willReturn(questionRequests); // isNotEmptyQuestions 통과용
      given(request.toQuestionEntities()).willReturn(questions);
      given(retroRepository.save(any(Retro.class))).willReturn(retro);

      // when
      Retro result = retroService.createRetro(memberId, request);

      // then
      assertThat(result).isNotNull();
      assertThat(result.getQuestions()).hasSize(1);
      assertThat(result.getQuestions().get(0).getRetro()).isEqualTo(result); // 양방향 연관관계 확인
      verify(retroRepository).save(retro);
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

      given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
      given(request.toEntity(member)).willReturn(retro);
      given(request.questions()).willReturn(Collections.emptyList()); // 질문 없음

      // when
      retroService.createRetro(memberId, request);

      // then
      assertThat(retro.getQuestions()).isEmpty();
      verify(request, never()).toQuestionEntities(); // 질문 변환 로직이 호출되지 않아야 함
      verify(retroRepository).save(retro);
    }

    @Test
    @DisplayName("실패: 회원이 존재하지 않으면 MEMBER_NOT_FOUND 예외를 던진다.")
    void failWhenMemberNotFound() {
      // given
      Long memberId = 1L;
      RetroCreateRequest request = mock(RetroCreateRequest.class);
      given(memberRepository.findById(memberId)).willReturn(Optional.empty());

      // when & then
      assertThatThrownBy(() -> retroService.createRetro(memberId, request))
          .isInstanceOf(BusinessException.class)
          .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MEMBER_NOT_FOUND);

      verify(retroRepository, never()).save(any());
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
  }
}