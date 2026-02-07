package com.retro.domain.notice.application.dto;

import java.time.LocalDateTime;

public record NoticeCreateResponse(Long noticeId, LocalDateTime createAt) {

  public static NoticeCreateResponse of(Long noticeId, LocalDateTime createAt) {
    return new NoticeCreateResponse(noticeId, createAt);
  }

}
