package com.retro.domain.retro.domain.event;

import com.retro.domain.retro.domain.entity.Retro;

public record RetroBlindedEvent(Long memberId, Long retroId) {

  public static RetroBlindedEvent of(Retro retro) {
    return new RetroBlindedEvent(retro.getMemberId(), retro.getRetroId());
  }
}
