package com.retro.domain.retro.domain.event;

import com.retro.domain.member.domain.event.MemberWithdrawnEvent;
import com.retro.domain.retro.application.RetroFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RetroEventConsumer {

  private final RetroFacade retroFacade;

  @EventListener
  public void consumeMemberWithdrawnEvent(MemberWithdrawnEvent memberWithdrawnEvent) {
    retroFacade.updateRetrosForWithdrawnMember(memberWithdrawnEvent.memberId());
  }
}
