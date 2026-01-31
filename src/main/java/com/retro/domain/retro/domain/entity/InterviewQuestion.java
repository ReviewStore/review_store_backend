package com.retro.domain.retro.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "interview_questions")
public class InterviewQuestion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long questionId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "retro_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Retro retro;

  @Column(nullable = false)
  private int questionOrder;

  @Column(nullable = false, length = 20)
  private String questionType;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String questionText;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String answerText;

  private String interviewerReaction;

  private int satisfactionScore;

  @Builder(access = AccessLevel.PRIVATE)
  private InterviewQuestion(int questionOrder, String questionType, String questionText,
      String answerText, String interviewerReaction, int satisfactionScore) {
    this.questionOrder = questionOrder;
    this.questionType = questionType;
    this.questionText = questionText;
    this.answerText = answerText;
    this.interviewerReaction = interviewerReaction;
    this.satisfactionScore = satisfactionScore;
  }

  public static InterviewQuestion of(int questionOrder, String questionType, String questionText,
      String answerText, String interviewerReaction, Integer satisfactionScore) {
    return InterviewQuestion.builder()
        .questionOrder(questionOrder)
        .questionType(questionType)
        .questionText(questionText)
        .answerText(answerText)
        .interviewerReaction(interviewerReaction)
        .satisfactionScore(satisfactionScore)
        .build();
  }

  protected void assignToRetro(Retro retro) {
    this.retro = retro;
  }
}