package com.retro.domain.member.domain.entity;

import lombok.Getter;

@Getter
public enum PostReadPermission {
  LIMITED,        // 제한 열람 (기본)
  UNLIMITED;      // 무제한 열람

  public boolean isUnlimited() {
    return this == UNLIMITED;
  }

  public boolean isLimited() {
    return this == LIMITED;
  }
}
