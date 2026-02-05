package com.retro.domain.retro.application.dto;

import com.retro.domain.retro.application.dto.response.RetroDetailResponse;
import java.util.List;

public record RetroCursorPageResponse(
    List<RetroDetailResponse> retros,
    Long nextCursor,
    boolean hasNext
) {

  public static RetroCursorPageResponse of(List<RetroDetailResponse> retros, Long nextCursor,
      boolean hasNext) {
    return new RetroCursorPageResponse(retros, nextCursor, hasNext);
  }
}