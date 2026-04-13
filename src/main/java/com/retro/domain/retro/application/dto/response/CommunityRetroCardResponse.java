package com.retro.domain.retro.application.dto.response;

import com.retro.domain.retro.domain.entity.Retro;
import java.time.LocalDate;

public record CommunityRetroCardResponse(
    Long retroId,
    String company,
    String position,
    LocalDate interviewDate,
    String interviewRound,
    String tags,
    String summary
) {

  public static CommunityRetroCardResponse from(Retro retro) {
    return new CommunityRetroCardResponse(
        retro.getRetroId(),
        retro.getCompanyName(),
        retro.getPosition(),
        retro.getInterviewDate(),
        retro.getInterviewRound(),
        retro.getInterviewTags(),
        retro.getSummary()
    );
  }
}