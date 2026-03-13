package com.retro.domain.auth.application.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public record TokenVerificationResponse(
    String message
) {

  // boolean 값을 직접 받아서 내부에서 분기 처리 후 객체 생성
  public static TokenVerificationResponse of(boolean isValid) {
    VerificationStatus status = VerificationStatus.from(isValid);
    return new TokenVerificationResponse(status.getMessage());
  }

  @Getter
  @RequiredArgsConstructor
  public enum VerificationStatus {
    VALID("유효한 토큰입니다."),
    INVALID("유효하지 않은 토큰입니다.");

    private final String message;

    public static VerificationStatus from(boolean isValid) {
      if (isValid) {
        return VALID;
      }
      return INVALID;
    }
  }
}