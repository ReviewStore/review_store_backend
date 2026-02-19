package com.retro.domain.notification.domain.event;

import com.retro.domain.auth.domain.event.RegistrationEvent;
import com.retro.domain.notification.application.NotificationFacade;
import com.retro.domain.retro.domain.event.RetroBlindedEvent;
import com.retro.domain.retro.domain.event.RetroReadLimitWarningEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventConsumer {

  private final NotificationFacade notificationFacade;

  @EventListener
  public void consumeRegistrationEvent(RegistrationEvent registrationEvent) {
    Long memberId = registrationEvent.memberId();
    notificationFacade.createWelcomeNotification(memberId);
  }

  @EventListener
  public void consumeRetroReadLimitWarningEvent(
      RetroReadLimitWarningEvent retroReadLimitWarningEvent) {
    Long memberId = retroReadLimitWarningEvent.memberId();
    notificationFacade.createReadPermissionGuideNotification(memberId);
  }

  @EventListener
  public void consumeRetroBlindedEvent(RetroBlindedEvent retroBlindedEvent) {
    notificationFacade.createRetroBlindedNotification(retroBlindedEvent.memberId());
  }
}
