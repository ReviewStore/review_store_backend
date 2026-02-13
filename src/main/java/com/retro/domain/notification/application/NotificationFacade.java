package com.retro.domain.notification.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationFacade {

  private final NotificationService notificationService;

  public void createWelcomeNotification(Long memberId) {
    notificationService.createWelcomeNotification(memberId);
  }


  public void createReadPermissionGuideNotification(Long memberId) {
    notificationService.createReadPermissionGuideNotification(memberId);
  }


}
