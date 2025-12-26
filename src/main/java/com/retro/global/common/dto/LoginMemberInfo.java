package com.retro.global.common.dto;

import com.retro.domain.member.domain.entity.Member;
import com.retro.domain.member.domain.entity.Role;
import lombok.Builder;


@Builder
public record LoginMemberInfo(Long id, Role role) {

  public static LoginMemberInfo of(Member member) {
    return LoginMemberInfo.builder()
        .id(member.getId())
        .role(member.getRole())
        .build();
  }
}
