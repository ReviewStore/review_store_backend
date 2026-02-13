package com.retro.domain.auth.domain.event;

import com.retro.domain.member.domain.entity.Member;

public record RegistrationEvent(Long memberId) {

  public static RegistrationEvent of(Member member) {
    return new RegistrationEvent(member.getId());
  }

}
