package com.retro.domain.auth.domain.event;

import com.retro.domain.member.domain.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthEventPublisher {

  private final ApplicationEventPublisher applicationEventPublisher;

  public void publishRegistrationEvent(Member member) {
    RegistrationEvent registrationEvent = RegistrationEvent.of(member);
    applicationEventPublisher.publishEvent(registrationEvent);
  }
}
