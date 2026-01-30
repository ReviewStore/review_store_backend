package com.retro.global.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

  // ===== 1000번대: 인증/회원 관련 =====
  INVALID_TOKEN(401, "AUTH-1001", "유효하지 않은 토큰입니다."),
  MEMBER_NOT_FOUND(401, "AUTH-1002", "유저를 찾을 수 없습니다."),
  TERMS_AGREEMENT_REQUIRED(403, "AUTH-1003", "약관 동의가 필요합니다."),
  OAUTH_AUTHENTICATION_FAILED(401, "AUTH-1004", "OAuth 인증에 실패했습니다."),

  // ===== 2000번대: 면접 후기 관련 =====
  RETRO_NOT_FOUND(404, "RETRO-2001", "존재하지 않는 면접 후기입니다."),
  RETRO_READ_POINT_EXCEEDED(403, "RETRO-2002", "회고 열람 가능 횟수를 초과했습니다."),

  // ===== 3000번대: 질문 관련 =====
  QUESTION_NOT_FOUND(404, "QUESTION-3001", "존재하지 않는 질문입니다."),

  // ===== 9000번대: 공통 에러 =====
  INVALID_INPUT_VALUE(400, "COMMON-9001", "유효하지 않은 입력값입니다."),
  UNAUTHORIZED(400, "COMMON-9002", "권한이 없습니다."),
  INTERNAL_SERVER_ERROR(500, "COMMON-9003", "서버 내부 오류가 발생했습니다.");


  private final int status;
  private final String code;
  private final String message;
}
