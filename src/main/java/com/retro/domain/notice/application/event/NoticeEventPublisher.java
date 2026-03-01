package com.retro.domain.notice.application.event;

import com.retro.domain.notice.domain.event.NoticeCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NoticeEventPublisher {

  private final ApplicationEventPublisher applicationEventPublisher;

  public void publishNoticeCreatedEvent(Long noticeId) {
    applicationEventPublisher.publishEvent(NoticeCreatedEvent.of(noticeId));
  }
}
