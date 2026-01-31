package com.retro.domain.retro.application.dto.response;

import com.retro.domain.retro.domain.entity.Retro;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record RetroCreateResponse(Long retroId, LocalDateTime createAt) {

  public static RetroCreateResponse of(Retro retro) {
    return RetroCreateResponse.builder()
        .retroId(retro.getRetroId())
        .createAt(retro.getCreatedAt())
        .build();
  }
}
