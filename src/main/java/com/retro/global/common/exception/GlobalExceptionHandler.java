package com.retro.global.common.exception;

import com.retro.global.common.dto.ErrorResponse;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j                 // 로그 자동 생성
@RestControllerAdvice  // 모든 컨트롤러 예외를 여기서 처리 !
public class GlobalExceptionHandler {

    /**
     * 비즈니스 로직 예외 처리
     */
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        log.error("BusinessException: {}", e.getMessage());
        if (StringUtils.hasText(e.getMessage())) {
            ErrorResponse response = ErrorResponse.of(e.getErrorCode(), e.getMessage());
            return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(response);
        }
        ErrorResponse response = ErrorResponse.of(e.getErrorCode());
        return ResponseEntity
            .status(e.getErrorCode().getStatus())
            .body(response);
    }

    /**
     * 모든 예외의 최상위 핸들러 (예상치 못한 에러)
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Unexpected Error: ", e);
        ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_TOKEN);
        return ResponseEntity.internalServerError().body(response);
    }

    /**
     * @Valid 검증 실패 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
        MethodArgumentNotValidException e) {
        log.error("Validation Error: {}", e.getMessage());
        ErrorResponse response = ErrorResponse.builder()
            .status(400)
            .code("COMMON-9001")
            .message(e.getBindingResult().getAllErrors().get(0).getDefaultMessage())
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.badRequest().body(response);
    }
}
