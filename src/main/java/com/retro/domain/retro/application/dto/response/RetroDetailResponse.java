package com.retro.domain.retro.application.dto.response;

import com.retro.domain.retro.domain.entity.InterviewQuestion;
import com.retro.domain.retro.domain.entity.Retro;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.springframework.util.CollectionUtils;

public record RetroDetailResponse(
    Long retroId,
    Long authorId,
    String company,
    String position,
    LocalDate interviewDate,
    String round,
    String tags,
    String keepText,
    String problemText,
    String tryText,
    String summary,
    List<QuestionResponse> questions
) {

  public static RetroDetailResponse from(Retro retro) {

    if (CollectionUtils.isEmpty(retro.getQuestions())) {
      return new RetroDetailResponse(
          retro.getRetroId(),
          retro.getMember().getId(),
          retro.getCompanyName(),
          retro.getPosition(),
          retro.getInterviewDate(),
          retro.getInterviewRound(),
          retro.getInterviewTags(),
          retro.getKeepText(),
          retro.getProblemText(),
          retro.getTryText(),
          retro.getSummary(),
          Collections.emptyList()
      );
    }

    List<QuestionResponse> questionResponses = retro.getQuestions()
        .stream()
        .map(QuestionResponse::from)
        .toList();

    return new RetroDetailResponse(
        retro.getRetroId(),
        retro.getMember().getId(),
        retro.getCompanyName(),
        retro.getPosition(),
        retro.getInterviewDate(),
        retro.getInterviewRound(),
        retro.getInterviewTags(),
        retro.getKeepText(),
        retro.getProblemText(),
        retro.getTryText(),
        retro.getSummary(),
        questionResponses
    );
  }

  public record QuestionResponse(
      int questionOrder,
      String questionType,
      String questionText,
      String answerText,
      String interviewerReaction,
      Integer satisfactionScore
  ) {

    public static QuestionResponse from(InterviewQuestion interviewQuestion) {
      return new QuestionResponse(
          interviewQuestion.getQuestionOrder(),
          interviewQuestion.getQuestionType(),
          interviewQuestion.getQuestionText(),
          interviewQuestion.getAnswerText(),
          interviewQuestion.getInterviewerReaction(),
          interviewQuestion.getSatisfactionScore()
      );
    }
  }

}
