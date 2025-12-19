package com.retro.global.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // ===== 1000번대: 인증/회원 관련 =====
    INVALID_TOKEN(401, "AUTH-1001", "유효하지 않은 토큰입니다."),
    MEMBER_NOT_FOUND(401, "AUTH-1002", "유저를 찾을 수 없습니다."),

    // ===== 2000번대: 면접 후기 관련 =====
    REVIEW_NOT_FOUND(404, "REVIEW-2001", "존재하지 않는 면접 후기입니다."),

    // ===== 3000번대: 질문 관련 =====
    QUESTION_NOT_FOUND(404, "QUESTION-3001", "존재하지 않는 질문입니다."),

    // ===== 9000번대: 공통 에러 =====
    INVALID_INPUT_VALUE(400, "COMMON-9001", "유효하지 않은 입력값입니다."),
    UNAUTHORIZED(400, "COMMON-9002", "권한이 없습니다.");

    private final int status;
    private final String code;
    private final String message;
}
