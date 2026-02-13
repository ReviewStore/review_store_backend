package com.retro.domain.retro.domain.event;

import com.retro.domain.member.domain.entity.Member;

public record RetroReadLimitWarningEvent(Long memberId) {

  public static RetroReadLimitWarningEvent of(Member member) {
    return new RetroReadLimitWarningEvent(member.getId());
  }
}
