package com.retro.domain.retro.application.dto.response;

import com.retro.domain.retro.domain.entity.Keyword;
import lombok.Builder;

@Builder
public record KeywordResponse(String name, String category) {

  public static KeywordResponse from(Keyword keyword) {
    return KeywordResponse.builder()
        .name(keyword.getName())
        .category(keyword.getCategory())
        .build();
  }
}
