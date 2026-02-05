package com.retro.domain.retro.application.dto.request;

import com.retro.domain.retro.domain.entity.InterviewQuestion;
import com.retro.domain.retro.domain.entity.Retro;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record RetroCreateRequest(
    @NotBlank(message = "회사명은 필수입니다.")
    String companyName,
    @NotBlank(message = "지원 직무는 필수입니다.")
    String position,
    @NotNull(message = "면접 일자는 필수입니다.")
    LocalDate interviewDate,
    @NotBlank(message = "면접 차수는 필수입니다.")
    String interviewRound,
    @Nullable
    String interviewTags,
    @Nullable
    String keepText,
    @Nullable
    String problemText,
    @Nullable
    String tryText,
    @Nullable
    String summary,
    @Nullable
    List<QuestionRequest> questions
) {

  public static RetroCreateRequest of(String companyName, String position, LocalDate interviewDate,
      String interviewRound, String interviewTags, String keepText,
      String problemText, String tryText, String summary,
      Boolean isPublic, List<QuestionRequest> questions) {
    return new RetroCreateRequest(companyName, position, interviewDate, interviewRound,
        interviewTags, keepText, problemText, tryText, summary,
        questions);
  }

  public Retro toEntity(Long memberId) {
    return Retro.of(memberId, companyName, position, interviewDate, interviewRound,
        interviewTags, keepText, problemText, tryText, summary);
  }

  public List<InterviewQuestion> toQuestionEntities() {
    return questions.stream()
        .map(QuestionRequest::toEntity)
        .toList();
  }

}