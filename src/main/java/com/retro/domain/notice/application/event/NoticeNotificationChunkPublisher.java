package com.retro.domain.notice.application.event;

import com.retro.domain.notice.domain.event.NoticeNotificationChunkEvent;
import com.retro.domain.notice.domain.event.NoticeNotificationFailureEvent;
import com.retro.global.config.NoticeNotificationRabbitProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NoticeNotificationChunkPublisher {

  private final RabbitTemplate rabbitTemplate;
  private final NoticeNotificationRabbitProperties rabbitProperties;

  public void publish(NoticeNotificationChunkEvent event) {
    rabbitTemplate.convertAndSend(
        rabbitProperties.exchange(),
        rabbitProperties.routingKey(),
        event
    );
  }

  public void publishFailure(NoticeNotificationFailureEvent event) {
    rabbitTemplate.convertAndSend(
        rabbitProperties.deadLetterExchange(),
        rabbitProperties.routingKey(),
        event
    );
  }
}
