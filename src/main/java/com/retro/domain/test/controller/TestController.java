package com.retro.domain.test.controller;

import com.retro.global.common.dto.ApiResponse;
import com.retro.global.common.exception.BusinessException;
import com.retro.global.common.exception.ErrorCode;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/test")
public class TestController {

    /**
     * 성공 응답 테스트
     */
    @GetMapping("/success")
    public ApiResponse<Map<String, String>> testSuccess() {
        Map<String, String> data = new HashMap<>();
        data.put("message", "공통 모듈 정상 작동!");
        data.put("status", "OK");

        return ApiResponse.success(data, "테스트 성공");
    }

    /**
     * 에러 응답 테스트 - BusinessException
     */
    @GetMapping("/error/{errorCode}")
    public ApiResponse<Void> testError(@PathVariable String errorCode) {
        switch (errorCode) {
            case "member":
                throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
            case "review":
                throw new BusinessException(ErrorCode.REVIEW_NOT_FOUND);
            case "auth":
                throw new BusinessException(ErrorCode.UNAUTHORIZED);
            default:
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    /**
     * 예상치 못한 에러 테스트 - Exception
     */
    @GetMapping("/error/unexpected")
    public ApiResponse<Void> testUnexpectedError() {
        // 일부러 NullPointerException 발생
        String nullString = null;
        nullString.length();  // NPE 발생!

        return ApiResponse.success();
    }

}
