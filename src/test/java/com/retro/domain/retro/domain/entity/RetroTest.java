package com.retro.domain.retro.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RetroTest {

  private Retro retro;

  @BeforeEach
  void setUp() {
    retro = Retro.of(null, "카카오", "백엔드", LocalDate.now(), "1차", "#Java", "K", "P", "T", "요약");
  }
  
  @Test
  @DisplayName("여러 개의 면접 질문을 추가할 수 있으며, 양방향 연관관계가 설정된다.")
  void addMultipleQuestions() {
    // given
    InterviewQuestion q1 = InterviewQuestion.of(1, "공통", "질문1", "답1", "상", 1);
    InterviewQuestion q2 = InterviewQuestion.of(2, "기술", "질문2", "답2", "중", 2);

    // when
    retro.addQuestion(q1);
    retro.addQuestion(q2);

    // then
    assertThat(retro.getQuestions()).hasSize(2);
    assertThat(q1.getRetro()).isEqualTo(retro);
    assertThat(q2.getRetro()).isEqualTo(retro);
  }

}