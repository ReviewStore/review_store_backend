package com.retro.domain.member.application.dto.response;

public record AppleAuthCodeDto(String authCode) {
  public static AppleAuthCodeDto of(String authCode){
    return new AppleAuthCodeDto(authCode);
  }
}
