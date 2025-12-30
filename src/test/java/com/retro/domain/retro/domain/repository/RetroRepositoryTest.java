package com.retro.domain.retro.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.retro.domain.member.domain.MemberRepository;
import com.retro.domain.member.domain.entity.Member;
import com.retro.domain.member.domain.entity.Provider;
import com.retro.domain.member.domain.entity.Term;
import com.retro.domain.member.infrastructure.MemberRepositoryImpl;
import com.retro.domain.retro.domain.entity.InterviewQuestion;
import com.retro.domain.retro.domain.entity.Retro;
import com.retro.domain.retro.infrastructure.RetroRepositoryImpl;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({RetroRepositoryImpl.class, MemberRepositoryImpl.class})
class RetroRepositoryTest {

  @Autowired
  private RetroRepository retroRepository;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private EntityManager em; // Cascade를 확실히 검증하기 위해 필요

  @Test
  @DisplayName("Cascade.ALL 검증: 회고를 저장하면 별도의 저장 호출 없이 면접 질문도 DB에 저장된다.")
  void cascadePersistTest() {
    // given
    Member member = createAndSaveMember();
    Retro retro = Retro.of(member, "카카오", "백엔드", LocalDate.now(), "1차", "#기술", "K", "P", "T", "요약");

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
    Retro retro = Retro.of(member, "네이버", "FE", LocalDate.now(), "2차", "#JS", "K", "P", "T", "요약");
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
    Retro retro = Retro.of(member, "라인", "iOS", LocalDate.now(), "1차", "#Swift", "K", "P", "T",
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

  // Member 생성 헬퍼 메서드
  private Member createAndSaveMember() {
    Term term = Term.from(true);
    Member member = Member.of(Provider.KAKAO, "test-id", "닉네임", term);
    return memberRepository.save(member);
  }
}