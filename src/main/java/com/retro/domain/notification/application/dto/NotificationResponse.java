package com.retro.domain.notification.application.dto;

import com.retro.domain.notification.domain.entity.Notification;
import com.retro.domain.notification.domain.entity.NotificationType;
import java.time.LocalDateTime;

public record NotificationResponse(
    Long notificationId,
    NotificationType type,
    String title,
    String content,
    String displayTime,
    LocalDateTime createdAt
) {

  public static NotificationResponse from(Notification notification, String displayTime) {
    return new NotificationResponse(
        notification.getNotificationId(),
        notification.getType(),
        notification.getTitle(),
        notification.getContent(),
        displayTime,
        notification.getCreatedAt()
    );
  }
}
