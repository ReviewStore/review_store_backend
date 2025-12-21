package com.retro.global.common.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

  @Getter
  @Builder
  @AllArgsConstructor
  public class JwtToken {
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpiredDate;
    private Long refreshTokenExpiredDate;
    private Long userId;
}
