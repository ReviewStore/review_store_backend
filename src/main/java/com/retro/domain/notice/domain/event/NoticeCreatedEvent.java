package com.retro.domain.notice.domain.event;

public record NoticeCreatedEvent(Long noticeId) {

  public static NoticeCreatedEvent of(Long noticeId) {
    return new NoticeCreatedEvent(noticeId);
  }
}