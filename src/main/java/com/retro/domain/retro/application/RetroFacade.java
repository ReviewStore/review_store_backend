package com.retro.domain.retro.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RetroFacade {

  private final RetroService retroService;

  public void updateRetrosForWithdrawnMember(Long memberId) {
    retroService.updateRetrosForWithdrawnMember(memberId);
  }
}
