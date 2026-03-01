package com.retro.domain.notice.domain.event;

public record NoticeNotificationFailureEvent(
    Long noticeId,
    Long cursorMemberId,
    int chunkSize,
    int retryCount,
    String reason
) {

  public static NoticeNotificationFailureEvent from(NoticeNotificationChunkEvent event,
      String reason) {
    return new NoticeNotificationFailureEvent(
        event.noticeId(),
        event.cursorMemberId(),
        event.chunkSize(),
        event.retryCount(),
        reason
    );
  }
}