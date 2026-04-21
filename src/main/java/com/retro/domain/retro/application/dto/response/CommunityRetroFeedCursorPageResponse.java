package com.retro.domain.retro.application.dto.response;

import java.util.List;

public record CommunityRetroFeedCursorPageResponse(
    List<CommunityRetroCardResponse> retros,
    Long nextCursor,
    boolean hasNext
) {

  public static CommunityRetroFeedCursorPageResponse of(
      List<CommunityRetroCardResponse> retros,
      Long nextCursor,
      boolean hasNext
  ) {
    return new CommunityRetroFeedCursorPageResponse(retros, nextCursor, hasNext);
  }
}