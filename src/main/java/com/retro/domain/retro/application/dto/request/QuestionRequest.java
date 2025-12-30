package com.retro.domain.retro.application.dto.request;

import com.retro.domain.retro.domain.entity.InterviewQuestion;

public record QuestionRequest(
    int questionOrder,
    String questionType,
    String questionText,
    String answerText,
    String interviewerReaction,
    int satisfactionScore
) {

  public static QuestionRequest of(int questionOrder, String questionType, String questionText,
      String answerText, String interviewerReaction, int satisfactionScore) {
    return new QuestionRequest(questionOrder, questionType, questionText,
        answerText, interviewerReaction, satisfactionScore);
  }

  public InterviewQuestion toEntity() {
    return InterviewQuestion.of(questionOrder, questionType, questionText,
        answerText, interviewerReaction, satisfactionScore);
  }
}