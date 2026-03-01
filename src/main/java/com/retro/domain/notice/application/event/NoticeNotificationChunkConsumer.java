package com.retro.domain.notice.application.event;

import com.retro.domain.member.application.MemberFacade;
import com.retro.domain.notice.application.NoticeNotificationDispatchService;
import com.retro.domain.notice.domain.event.NoticeNotificationChunkEvent;
import com.retro.domain.notice.domain.event.NoticeNotificationFailureEvent;
import com.retro.domain.notification.application.NotificationFacade;
import com.retro.global.config.NoticeNotificationRabbitProperties;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class NoticeNotificationChunkConsumer {

  private final MemberFacade memberFacade;
  private final NotificationFacade notificationFacade;
  private final NoticeNotificationChunkPublisher chunkPublisher;
  private final NoticeNotificationDispatchService noticeNotificationDispatchService;
  private final NoticeNotificationRabbitProperties rabbitProperties;

  @Transactional
  @RabbitListener(queues = "${app.rabbitmq.notice.queue}")
  public void consumeNoticeNotificationChunk(NoticeNotificationChunkEvent event) {
    try {
      List<Long> memberIds = memberFacade.findMemberIdsWithCursor(
          event.cursorMemberId(),
          event.chunkSize()
      );

      if (memberIds.isEmpty()) {
        noticeNotificationDispatchService.complete(event.noticeId());
        return;
      }

      notificationFacade.createNoticeNotifications(memberIds);
      Long lastCursor = memberIds.getLast();

      noticeNotificationDispatchService.progress(
          event.noticeId(),
          memberIds.size(),
          lastCursor
      );

      if (memberIds.size() == event.chunkSize()) {
        chunkPublisher.publish(event.next(lastCursor));
        return;
      }

      noticeNotificationDispatchService.complete(event.noticeId());
    } catch (Exception exception) {
      if (event.retryCount() < rabbitProperties.maxRetryCount()) {
        chunkPublisher.publish(event.retry());
        return;
      }

      noticeNotificationDispatchService.fail(event.noticeId(), exception.getMessage());
      chunkPublisher.publishFailure(
          NoticeNotificationFailureEvent.from(event, exception.getMessage()));
    }
  }
}