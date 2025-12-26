package com.retro.domain.member.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
  MEMBER("ROLE_MEMBER"),
  ADMIN("ROLE_ADMIN");

  private final String code;

  public static Role fromCode(String code) {
    for (Role role : values()) {
      if (role.code.equals(code)) {
        return role;
      }
    }
    throw new IllegalArgumentException("Unknown role code: " + code);
  }
}
