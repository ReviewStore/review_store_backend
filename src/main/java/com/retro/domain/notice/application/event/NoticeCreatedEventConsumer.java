package com.retro.domain.notice.application.event;

import com.retro.domain.notice.domain.event.NoticeCreatedEvent;
import com.retro.domain.notice.domain.event.NoticeNotificationChunkEvent;
import com.retro.global.config.NoticeNotificationRabbitProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NoticeCreatedEventConsumer {

  private final NoticeNotificationChunkPublisher chunkPublisher;
  private final NoticeNotificationRabbitProperties rabbitProperties;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void consumeNoticeCreatedEvent(NoticeCreatedEvent event) {
    chunkPublisher.publish(NoticeNotificationChunkEvent.first(
        event.noticeId(),
        rabbitProperties.chunkSize()
    ));
  }
}