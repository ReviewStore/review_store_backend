package com.retro.domain.notification.application.dto;

import java.util.List;

public record NotificationCursorPageResponse(
    List<NotificationResponse> notifications,
    Long nextCursor,
    boolean hasNext
) {

  public static NotificationCursorPageResponse of(List<NotificationResponse> notifications,
      Long nextCursor, boolean hasNext) {
    return new NotificationCursorPageResponse(notifications, nextCursor, hasNext);
  }
}
