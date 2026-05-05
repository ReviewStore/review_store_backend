package com.retro.domain.notice.application.dto;

import java.util.List;

public record NoticeCursorPageResponse(
    List<NoticeResponse> notices,
    Long nextCursor,
    boolean hasNext
) {

  public static NoticeCursorPageResponse of(List<NoticeResponse> notices, Long nextCursor,
      boolean hasNext) {
    return new NoticeCursorPageResponse(notices, nextCursor, hasNext);
  }

}

