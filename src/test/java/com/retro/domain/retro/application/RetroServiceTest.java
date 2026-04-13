package com.retro.domain.retro.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.retro.domain.member.application.MemberFacade;
import com.retro.domain.member.domain.MemberRepository;
import com.retro.domain.member.domain.entity.Member;
import com.retro.domain.retro.application.dto.RetroCursorPageResponse;
import com.retro.domain.retro.application.dto.request.QuestionRequest;
import com.retro.domain.retro.application.dto.request.RetroCreateRequest;
import com.retro.domain.retro.application.dto.response.CommunityRetroFeedCursorPageResponse;
import com.retro.domain.retro.application.dto.response.KeywordResponse;
import com.retro.domain.retro.application.dto.response.RetroDetailResponse;
import com.retro.domain.retro.domain.entity.InterviewQuestion;
import com.retro.domain.retro.domain.entity.Keyword;
import com.retro.domain.retro.domain.entity.Retro;
import com.retro.domain.retro.domain.event.RetroBlindedEvent;
import com.retro.domain.retro.domain.event.RetroEventPublisher;
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
import org.springframework.test.util.ReflectionTestUtils;

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

  @Mock
  private RetroEventPublisher retroEventPublisher;

  @Test
  @DisplayName("성공: 탈퇴한 회원의 회고 작성자 ID를 일괄 변경한다")
  void updateRetrosForWithdrawnMember() {

    // given
    Long memberId = 1L;
    Retro retro1 = Retro.of(memberId, "네이버1", "FE", LocalDate.now(), "2차", "#React", "K", "P",
        "T",
        "요약");
    Retro retro2 = Retro.of(memberId, "네이버2", "FE", LocalDate.now(), "2차", "#React", "K", "P",
        "T",
        "요약");
    Retro retro3 = Retro.of(memberId, "네이버3", "FE", LocalDate.now(), "2차", "#React", "K", "P",
        "T",
        "요약");
    List<Retro> retros = List.of(retro1, retro2, retro3);

    given(retroRepository.findAllByMemberId(memberId)).willReturn(retros);

    // when
    retroService.updateRetrosForWithdrawnMember(memberId);

    // then
    verify(retroRepository).findAllByMemberId(memberId);
  }

  @Test
  @DisplayName("실패: 블라인드된 회고는 RETRO_BLINDED")
  void fail_retroBlinded() {
    // given
    Long viewerId = 1L;
    Long retroId = 999L;

    Member viewer = mock(Member.class);
    Retro retro = mock(Retro.class);
    given(memberFacade.getMember(viewerId)).willReturn(viewer);
    given(retroRepository.findById(retroId)).willReturn(Optional.of(retro));
    given(retro.isBlindedRetro()).willReturn(true);

    // when & then
    assertThatThrownBy(() -> retroService.getRetro(viewerId, retroId))
        .isInstanceOf(BusinessException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RETRO_BLINDED);

    verify(viewer, never()).reduceRemainingPostReadCount();
  }

  @Nested
  @DisplayName("회고 신고(reportRetro)")
  class ReportRetro {

    @Test
    @DisplayName("성공: 신고 2회 누적 시 블라인드 이벤트를 발행한다")
    void success_publishBlindedEvent_whenBlindThresholdReached() {
      // given
      Long retroId = 1L;
      Long reporterId = 2L;
      Retro retro = Retro.of(10L, "네이버", "BE", LocalDate.now(), "1차", "#Java", "K", "P", "T", "요약");
      retro.report();

      given(retroRepository.findById(anyLong())).willReturn(Optional.of(retro));
      given(retroRepository.existsReportByRetroAndReporter(anyLong(), anyLong())).willReturn(
          Optional.empty());
      doNothing().when(retroEventPublisher).publishRetroBlindedEvent(any(RetroBlindedEvent.class));

      // when
      retroService.reportRetro(retroId, reporterId);

      // then
      verify(retroEventPublisher).publishRetroBlindedEvent(any(RetroBlindedEvent.class));
      assertThat(retro.isBlindedRetro()).isTrue();
    }
  }

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

      Retro retro = Retro.of(memberId, "카카오", "백엔드", LocalDate.now(), "1차", "#Java", "K", "P", "T",
          "요약");

      List<InterviewQuestion> questions = List.of(
          InterviewQuestion.of(1, "기술", "JVM이란?", "답변", "상", 3)
      );

      given(memberFacade.getMember(memberId)).willReturn(member);
      given(member.getId()).willReturn(memberId);
      given(request.toEntity(memberId)).willReturn(retro);
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

      Retro retro = Retro.of(memberId, "네이버", "FE", LocalDate.now(), "2차", "#React", "K", "P", "T",
          "요약");

      given(member.getId()).willReturn(memberId);
      given(memberFacade.getMember(memberId)).willReturn(member);
      given(request.toEntity(memberId)).willReturn(retro);
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

      given(member.getId()).willReturn(memberId);
      given(memberFacade.getMember(memberId)).willReturn(member);
      given(request.toEntity(memberId)).willReturn(retro);
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
      Long authorId = 1L;

      Member viewer = mock(Member.class);
      Retro retro = mock(Retro.class);

      given(memberFacade.getMember(viewerId)).willReturn(viewer);
      given(retroRepository.findById(retroId)).willReturn(Optional.of(retro));
      given(retro.getMemberId()).willReturn(authorId);
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
      Long authorId = 3L;

      Member viewer = mock(Member.class);
      Retro retro = mock(Retro.class);

      given(memberFacade.getMember(viewerId)).willReturn(viewer);
      given(retroRepository.findById(retroId)).willReturn(Optional.of(retro));
      given(retro.getMemberId()).willReturn(authorId);
      given(retro.isCreatedByViewer(authorId, viewerId)).willReturn(false);
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
      Long authorId = 3L;

      Member viewer = mock(Member.class);
      Retro retro = mock(Retro.class);

      given(memberFacade.getMember(viewerId)).willReturn(viewer);
      given(retroRepository.findById(retroId)).willReturn(Optional.of(retro));
      given(retro.getMemberId()).willReturn(authorId);
      given(retro.isCreatedByViewer(authorId, viewerId)).willReturn(false);
      given(viewer.isPostReadCountExceeded()).willReturn(false);

      // when
      RetroDetailResponse response = retroService.getRetro(viewerId, retroId);

      // then
      assertThat(response).isNotNull();
      verify(viewer).reduceRemainingPostReadCount();
    }
  }

  @Nested
  @DisplayName("내 회고 목록 조회(getMyRetros)")
  class GetMyRetros {

    @Test
    @DisplayName("성공: size+1 조회 시 다음 커서와 hasNext=true 반환")
    void success_hasNext() {
      // given
      Long memberId = 1L;
      Long cursorId = null;
      int size = 2;
      Member member = mock(Member.class);

      Retro retro1 = Retro.of(memberId, "네이버1", "FE", LocalDate.now(), "2차", "#React", "K", "P",
          "T",
          "요약");
      Retro retro2 = Retro.of(memberId, "네이버2", "FE", LocalDate.now(), "2차", "#React", "K", "P",
          "T",
          "요약");
      Retro retro3 = Retro.of(memberId, "네이버3", "FE", LocalDate.now(), "2차", "#React", "K", "P",
          "T",
          "요약");
      ReflectionTestUtils.setField(retro1, "retroId", 1L);
      ReflectionTestUtils.setField(retro2, "retroId", 2L);
      ReflectionTestUtils.setField(retro3, "retroId", 3L);

      given(memberFacade.getMember(memberId)).willReturn(member);
      given(member.getId()).willReturn(memberId);
      given(retroRepository.findByMemberIdWithCursor(memberId, cursorId, size + 1))
          .willReturn(List.of(retro1, retro2, retro3));

      // when
      RetroCursorPageResponse response = retroService.getMyRetros(memberId, cursorId, size);

      // then
      assertThat(response.retros()).hasSize(2);
      assertThat(response.hasNext()).isTrue();
      assertThat(response.nextCursor()).isEqualTo(2L);
    }

    @Test
    @DisplayName("성공: 추가 데이터가 없으면 hasNext=false 및 nextCursor=null 반환")
    void success_noNext() {
      // given
      Long memberId = 1L;
      Long cursorId = 5L;
      int size = 3;
      Member member = mock(Member.class);

      Retro retro1 = Retro.of(memberId, "네이버1", "FE", LocalDate.now(), "2차", "#React", "K", "P",
          "T",
          "요약");
      Retro retro2 = Retro.of(memberId, "네이버2", "FE", LocalDate.now(), "2차", "#React", "K", "P",
          "T",
          "요약");
      ReflectionTestUtils.setField(retro1, "retroId", 1L);
      ReflectionTestUtils.setField(retro2, "retroId", 2L);

      given(memberFacade.getMember(memberId)).willReturn(member);
      given(member.getId()).willReturn(memberId);
      given(retroRepository.findByMemberIdWithCursor(memberId, cursorId, size + 1))
          .willReturn(List.of(retro1, retro2));

      // when
      RetroCursorPageResponse response = retroService.getMyRetros(memberId, cursorId, size);

      // then
      assertThat(response.retros()).hasSize(2);
      assertThat(response.hasNext()).isFalse();
      assertThat(response.nextCursor()).isNull();
    }
  }

  @Nested
  @DisplayName("커뮤니티 피드 검색/필터링(searchCommunityFeed)")
  class SearchCommunityFeed {

    private static final int SIZE = 2;
    private static final int PAGE_SIZE_PLUS_ONE = 3;

    private Retro makeRetro(Long memberId, String company, String position, String round,
        String tags, Long retroId) {
      Retro r = Retro.of(memberId, company, position, LocalDate.now(), round, tags, "K", "P", "T",
          "요약");
      ReflectionTestUtils.setField(r, "retroId", retroId);
      return r;
    }

    @Test
    @DisplayName("성공: 조건 없이 전체 공개 회고 목록을 반환한다")
    void success_noFilter() {
      // given
      Retro r1 = makeRetro(1L, "네이버", "백엔드", "1차", "#Java", 101L);
      Retro r2 = makeRetro(2L, "카카오", "프론트엔드", "2차", "#React", 100L);

      given(retroRepository.searchCommunityFeed(null, null, null, null, PAGE_SIZE_PLUS_ONE))
          .willReturn(List.of(r1, r2));

      // when
      CommunityRetroFeedCursorPageResponse response =
          retroService.searchCommunityFeed(null, null, null, null, SIZE);

      // then
      assertThat(response.retros()).hasSize(2);
      assertThat(response.hasNext()).isFalse();
      assertThat(response.nextCursor()).isNull();
    }

    @Test
    @DisplayName("성공: keyword 조건으로 회사명이 일치하는 회고를 반환한다")
    void success_filterByKeyword() {
      // given
      Retro r1 = makeRetro(1L, "네이버", "백엔드", "1차", "#Java", 101L);

      given(retroRepository.searchCommunityFeed("네이버", null, null, null, PAGE_SIZE_PLUS_ONE))
          .willReturn(List.of(r1));

      // when
      CommunityRetroFeedCursorPageResponse response =
          retroService.searchCommunityFeed("네이버", null, null, null, SIZE);

      // then
      assertThat(response.retros()).hasSize(1);
      assertThat(response.retros().get(0).company()).isEqualTo("네이버");
    }

    @Test
    @DisplayName("성공: position 조건으로 직무가 일치하는 회고를 반환한다")
    void success_filterByPosition() {
      // given
      Retro r1 = makeRetro(1L, "라인", "백엔드", "1차", "#Go", 101L);

      given(retroRepository.searchCommunityFeed(null, "백엔드", null, null, PAGE_SIZE_PLUS_ONE))
          .willReturn(List.of(r1));

      // when
      CommunityRetroFeedCursorPageResponse response =
          retroService.searchCommunityFeed(null, "백엔드", null, null, SIZE);

      // then
      assertThat(response.retros()).hasSize(1);
      assertThat(response.retros().get(0).position()).isEqualTo("백엔드");
    }

    @Test
    @DisplayName("성공: interviewRound 조건으로 면접 차수가 일치하는 회고를 반환한다")
    void success_filterByInterviewRound() {
      // given
      Retro r1 = makeRetro(1L, "토스", "iOS", "2차", "#Swift", 101L);

      given(retroRepository.searchCommunityFeed(null, null, "2차", null, PAGE_SIZE_PLUS_ONE))
          .willReturn(List.of(r1));

      // when
      CommunityRetroFeedCursorPageResponse response =
          retroService.searchCommunityFeed(null, null, "2차", null, SIZE);

      // then
      assertThat(response.retros()).hasSize(1);
      assertThat(response.retros().get(0).interviewRound()).isEqualTo("2차");
    }

    @Test
    @DisplayName("성공: 페이지 크기 초과 시 hasNext=true와 nextCursor를 반환한다")
    void success_hasNextTrue() {
      // given
      Retro r1 = makeRetro(1L, "A사", "백엔드", "1차", "#A", 103L);
      Retro r2 = makeRetro(2L, "B사", "프론트엔드", "1차", "#B", 102L);
      Retro r3 = makeRetro(3L, "C사", "데브옵스", "1차", "#C", 101L);

      given(retroRepository.searchCommunityFeed(null, null, null, null, PAGE_SIZE_PLUS_ONE))
          .willReturn(List.of(r1, r2, r3));

      // when
      CommunityRetroFeedCursorPageResponse response =
          retroService.searchCommunityFeed(null, null, null, null, SIZE);

      // then
      assertThat(response.retros()).hasSize(2);
      assertThat(response.hasNext()).isTrue();
      assertThat(response.nextCursor()).isEqualTo(102L);
    }

    @Test
    @DisplayName("성공: 검색 결과가 없으면 빈 목록과 hasNext=false를 반환한다")
    void success_emptyResult() {
      // given
      given(retroRepository.searchCommunityFeed("없는회사", null, null, null, PAGE_SIZE_PLUS_ONE))
          .willReturn(Collections.emptyList());

      // when
      CommunityRetroFeedCursorPageResponse response =
          retroService.searchCommunityFeed("없는회사", null, null, null, SIZE);

      // then
      assertThat(response.retros()).isEmpty();
      assertThat(response.hasNext()).isFalse();
      assertThat(response.nextCursor()).isNull();
    }
  }

}