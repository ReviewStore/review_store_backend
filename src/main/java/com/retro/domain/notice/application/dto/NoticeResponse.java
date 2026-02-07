package com.retro.domain.notice.application.dto;

import com.retro.domain.notice.domain.entity.Notice;
import java.time.LocalDateTime;

public record NoticeResponse(Long noticeId, String title, String content,
                             LocalDateTime createdAt) {

  public static NoticeResponse from(Notice notice) {
    return new NoticeResponse(
        notice.getNoticeId(),
        notice.getTitle(),
        notice.getContent(),
        notice.getCreatedAt()
    );
  }
}