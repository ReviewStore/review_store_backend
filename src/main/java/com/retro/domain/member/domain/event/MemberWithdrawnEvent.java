package com.retro.domain.member.domain.event;

public record MemberWithdrawnEvent(Long memberId) {

  public static MemberWithdrawnEvent of(Long memberId) {
    return new MemberWithdrawnEvent(memberId);
  }
}