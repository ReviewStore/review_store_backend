package com.retro.domain.member.domain.event;

import com.retro.domain.member.domain.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberEventPublisher {

  private final ApplicationEventPublisher applicationEventPublisher;

  public void publishMemberWithdrawnEvent(Member member) {
    MemberWithdrawnEvent memberWithdrawnEvent = MemberWithdrawnEvent.of(member.getId());
    applicationEventPublisher.publishEvent(memberWithdrawnEvent);
  }


}
