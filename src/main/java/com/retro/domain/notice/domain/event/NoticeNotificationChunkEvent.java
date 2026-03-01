package com.retro.domain.notice.domain.event;

public record NoticeNotificationChunkEvent(
    Long noticeId,
    Long cursorMemberId,
    int chunkSize,
    int retryCount
) {

  public static NoticeNotificationChunkEvent first(Long noticeId, int chunkSize) {
    return new NoticeNotificationChunkEvent(noticeId, null, chunkSize, 0);
  }

  public NoticeNotificationChunkEvent next(Long nextCursorMemberId) {
    return new NoticeNotificationChunkEvent(noticeId, nextCursorMemberId, chunkSize, 0);
  }

  public NoticeNotificationChunkEvent retry() {
    return new NoticeNotificationChunkEvent(noticeId, cursorMemberId, chunkSize, retryCount + 1);
  }
}