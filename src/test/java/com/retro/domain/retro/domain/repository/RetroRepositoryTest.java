package com.retro.domain.retro.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.retro.domain.member.domain.MemberRepository;
import com.retro.domain.member.domain.entity.Member;
import com.retro.domain.member.domain.entity.Provider;
import com.retro.domain.member.domain.entity.Term;
import com.retro.domain.member.infrastructure.MemberRepositoryCustomImpl;
import com.retro.domain.member.infrastructure.MemberRepositoryImpl;
import com.retro.domain.retro.domain.entity.InterviewQuestion;
import com.retro.domain.retro.domain.entity.Retro;
import com.retro.domain.retro.infrastructure.RetroRepositoryCustomImpl;
import com.retro.domain.retro.infrastructure.RetroRepositoryImpl;
import com.retro.global.config.QuerydslConfig;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({RetroRepositoryImpl.class, MemberRepositoryImpl.class, MemberRepositoryCustomImpl.class,
    RetroRepositoryCustomImpl.class,
    QuerydslConfig.class})
class RetroRepositoryTest {

  @Autowired
  private RetroRepository retroRepository;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private EntityManager em; // Cascade를 확실히 검증하기 위해 필요


  @Test
  @DisplayName("커뮤니티 피드 조회: 공개 회원의 블라인드되지 않은 회고만 조회된다.")
  void findPublicRetrosWithCursor_filtersByMemberPublicAndBlinded() {
    // given
    int querySize = 10;
    Member publicMember = createAndSaveMember();
    publicMember.openOwnPublication();

    Member privateMember = createAndSaveMember();

    Retro publicRetro = Retro.of(publicMember.getId(), "네이버", "BE", LocalDate.now(), "1차", "#Java",
        "K", "P",
        "T", "공개");
    Retro privateRetro = Retro.of(privateMember.getId(), "카카오", "FE", LocalDate.now(), "1차",
        "#React", "K", "P",
        "T", "비공개");
    Retro blindedRetro = Retro.of(publicMember.getId(), "라인", "iOS", LocalDate.now(), "1차",
        "#Swift", "K", "P",
        "T", "블라인드");
    blindedRetro.report();
    blindedRetro.report();

    memberRepository.save(publicMember);
    memberRepository.save(privateMember);
    retroRepository.save(publicRetro);
    retroRepository.save(privateRetro);
    retroRepository.save(blindedRetro);
    em.flush();
    em.clear();

    // when
    var result = retroRepository.findPublicRetrosWithCursor(null, querySize);

    // then
    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getCompanyName()).isEqualTo("네이버");
    assertThat(result.getFirst().getPosition()).isEqualTo("BE");
  }

  @Test
  @DisplayName("Cascade.ALL 검증: 회고를 저장하면 별도의 저장 호출 없이 면접 질문도 DB에 저장된다.")
  void cascadePersistTest() {
    // given
    Member member = createAndSaveMember();
    Retro retro = Retro.of(member.getId(), "카카오", "백엔드", LocalDate.now(), "1차", "#기술", "K", "P",
        "T", "요약");

    InterviewQuestion question = InterviewQuestion.of(1, "직무", "JVM", "답", "상", 5);
    retro.addQuestion(question);

    // when
    retroRepository.save(retro);
    em.flush(); // DB에 SQL 반영
    em.clear(); // 영속성 컨텍스트 비우기 (DB에서 직접 조회 유도)

    // then
    Retro savedRetro = retroRepository.findById(retro.getRetroId()).orElseThrow();
    assertThat(savedRetro.getQuestions()).hasSize(1);
    assertThat(savedRetro.getQuestions().get(0).getQuestionText()).isEqualTo("JVM");
  }

  @Test
  @DisplayName("OrphanRemoval 검증: 회고의 질문 리스트에서 객체를 제거하면 DB에서도 삭제되어야 한다.")
  void orphanRemovalTest() {
    // given
    Member member = createAndSaveMember();
    Retro retro = Retro.of(member.getId(), "네이버", "FE", LocalDate.now(), "2차", "#JS", "K", "P", "T",
        "요약");
    retro.addQuestion(InterviewQuestion.of(1, "공통", "자기소개", "답", "상", 5));
    retroRepository.save(retro);
    em.flush();
    em.clear();

    // when
    Retro foundRetro = retroRepository.findById(retro.getRetroId()).orElseThrow();
    foundRetro.getQuestions().remove(0); // 리스트에서 질문 제거
    em.flush(); // DELETE 쿼리 발생
    em.clear();

    // then
    Retro resultRetro = retroRepository.findById(retro.getRetroId()).orElseThrow();
    assertThat(resultRetro.getQuestions()).isEmpty();
  }

  @Test
  @DisplayName("Cascade.REMOVE 검증: 회고 자체를 삭제하면 연관된 모든 질문도 삭제된다.")
  void cascadeRemoveTest() {
    // given
    Member member = createAndSaveMember();
    Retro retro = Retro.of(member.getId(), "라인", "iOS", LocalDate.now(), "1차", "#Swift", "K", "P",
        "T",
        "요약");
    retro.addQuestion(InterviewQuestion.of(1, "기술", "ARC란?", "답", "상", 5));
    retroRepository.save(retro);
    em.flush();

    Long questionId = retro.getQuestions().get(0).getQuestionId(); // 저장된 질문 ID 백업

    // when
    retroRepository.delete(retro);
    em.flush();
    em.clear();

    // then
    assertThat(retroRepository.findById(retro.getRetroId())).isEmpty();
    // 질문도 직접 조회 시 없어야 함
    InterviewQuestion deletedQuestion = em.find(InterviewQuestion.class, questionId);
    assertThat(deletedQuestion).isNull();
  }

  @Nested
  @DisplayName("커뮤니티 피드 검색/필터링(searchCommunityFeed)")
  class SearchCommunityFeed {

    @Test
    @DisplayName("성공: 조건 없이 조회하면 공개 회원의 비블라인드 회고만 반환한다")
    void noFilter_returnsOnlyPublicAndNotBlinded() {
      // given
      Member publicMember = createPublicMember();
      Member privateMember = createAndSaveMember();

      saveRetro(publicMember.getId(), "네이버", "백엔드", "1차", "#Java");
      saveRetro(privateMember.getId(), "카카오", "프론트엔드", "1차", "#React");
      em.flush();
      em.clear();

      // when
      List<Retro> result = retroRepository.searchCommunityFeed(null, null, null, null, 10);

      // then
      assertThat(result).hasSize(1);
      assertThat(result.get(0).getCompanyName()).isEqualTo("네이버");
    }

    @Test
    @DisplayName("성공: keyword로 회사명을 검색하면 포함하는 회고만 반환한다")
    void filterByKeyword_matchesCompanyName() {
      // given
      Member member = createPublicMember();
      saveRetro(member.getId(), "네이버", "백엔드", "1차", "#Java");
      saveRetro(member.getId(), "카카오", "백엔드", "1차", "#Kotlin");
      em.flush();
      em.clear();

      // when
      List<Retro> result = retroRepository.searchCommunityFeed("네이버", null, null, null, 10);

      // then
      assertThat(result).hasSize(1);
      assertThat(result.get(0).getCompanyName()).isEqualTo("네이버");
    }

    @Test
    @DisplayName("성공: keyword로 태그를 검색하면 포함하는 회고만 반환한다")
    void filterByKeyword_matchesTags() {
      // given
      Member member = createPublicMember();
      saveRetro(member.getId(), "라인", "백엔드", "1차", "#Spring #Java");
      saveRetro(member.getId(), "토스", "백엔드", "1차", "#Node");
      em.flush();
      em.clear();

      // when
      List<Retro> result = retroRepository.searchCommunityFeed("Spring", null, null, null, 10);

      // then
      assertThat(result).hasSize(1);
      assertThat(result.get(0).getCompanyName()).isEqualTo("라인");
    }

    @Test
    @DisplayName("성공: position 조건으로 직무가 일치하는 회고만 반환한다")
    void filterByPosition() {
      // given
      Member member = createPublicMember();
      saveRetro(member.getId(), "네이버", "백엔드", "1차", "#Java");
      saveRetro(member.getId(), "네이버", "프론트엔드", "1차", "#React");
      em.flush();
      em.clear();

      // when
      List<Retro> result = retroRepository.searchCommunityFeed(null, "백엔드", null, null, 10);

      // then
      assertThat(result).hasSize(1);
      assertThat(result.get(0).getPosition()).isEqualTo("백엔드");
    }

    @Test
    @DisplayName("성공: interviewRound 조건으로 면접 차수가 일치하는 회고만 반환한다")
    void filterByInterviewRound() {
      // given
      Member member = createPublicMember();
      saveRetro(member.getId(), "토스", "iOS", "1차", "#Swift");
      saveRetro(member.getId(), "토스", "iOS", "2차", "#Swift");
      em.flush();
      em.clear();

      // when
      List<Retro> result = retroRepository.searchCommunityFeed(null, null, "2차", null, 10);

      // then
      assertThat(result).hasSize(1);
      assertThat(result.get(0).getInterviewRound()).isEqualTo("2차");
    }

    @Test
    @DisplayName("성공: cursorId 기준으로 그보다 작은 retroId의 회고만 반환한다")
    void filterByCursorId() {
      // given
      Member member = createPublicMember();
      Retro r1 = saveRetro(member.getId(), "A사", "백엔드", "1차", "#A");
      Retro r2 = saveRetro(member.getId(), "B사", "백엔드", "1차", "#B");
      Retro r3 = saveRetro(member.getId(), "C사", "백엔드", "1차", "#C");
      em.flush();
      em.clear();

      Long cursorId = r3.getRetroId();

      // when
      List<Retro> result = retroRepository.searchCommunityFeed(null, null, null, cursorId, 10);

      // then
      assertThat(result).hasSize(2);
      assertThat(result).noneMatch(r -> r.getRetroId().equals(r3.getRetroId()));
    }

    @Test
    @DisplayName("성공: 검색 결과가 없으면 빈 리스트를 반환한다")
    void noMatch_returnsEmptyList() {
      // given
      Member member = createPublicMember();
      saveRetro(member.getId(), "네이버", "백엔드", "1차", "#Java");
      em.flush();
      em.clear();

      // when
      List<Retro> result = retroRepository.searchCommunityFeed("존재하지않는회사", null, null, null, 10);

      // then
      assertThat(result).isEmpty();
    }
  }

  // Member 생성 헬퍼 메서드
  private Member createAndSaveMember() {
    Term term = Term.from(true);
    Member member = Member.of(Provider.KAKAO, UUID.randomUUID().toString(), "닉네임", term);
    return memberRepository.save(member);
  }

  private Member createPublicMember() {
    Member member = createAndSaveMember();
    member.openOwnPublication();
    memberRepository.save(member);
    return member;
  }

  private Retro saveRetro(Long memberId, String company, String position, String round,
      String tags) {
    Retro retro = Retro.of(memberId, company, position, LocalDate.now(), round, tags, "K", "P",
        "T", "요약");
    return retroRepository.save(retro);
  }
}