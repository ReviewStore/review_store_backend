package com.retro.domain.member.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
@AllArgsConstructor
@Getter
public enum Role {
  MEMBER("ROLE_MEMBER"), ADMIN("ROLE_ADMIN");

  private final String code;

  }
