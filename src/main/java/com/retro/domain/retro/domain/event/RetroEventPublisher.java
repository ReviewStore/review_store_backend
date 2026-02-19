package com.retro.domain.retro.domain.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RetroEventPublisher {

  private final ApplicationEventPublisher applicationEventPublisher;

  public void publishRetroReadLimitWarningEvent(
      RetroReadLimitWarningEvent retroReadLimitWarningEvent) {
    applicationEventPublisher.publishEvent(retroReadLimitWarningEvent);
  }

  public void publishRetroBlindedEvent(RetroBlindedEvent retroBlindedEvent) {
    applicationEventPublisher.publishEvent(retroBlindedEvent);
  }
}
