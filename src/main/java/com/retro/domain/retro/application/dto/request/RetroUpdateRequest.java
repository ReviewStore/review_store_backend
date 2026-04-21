package com.retro.domain.retro.application.dto.request;

import com.retro.domain.retro.domain.entity.InterviewQuestion;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record RetroUpdateRequest(
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

  public List<InterviewQuestion> toQuestionEntities() {
    if (questions == null) {
      return null;
    }
    return questions.stream()
        .map(QuestionRequest::toEntity)
        .toList();
  }

}
