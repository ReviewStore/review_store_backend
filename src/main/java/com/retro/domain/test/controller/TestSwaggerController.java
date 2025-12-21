package com.retro.domain.test.controller;

import com.retro.global.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "테스트", description = "Swagger 테스트용 API")
@RestController
@RequestMapping("/api/test")
public class TestSwaggerController {

    @Operation(summary = "헬스 체크", description = "서버 상태를 확인합니다.")
    @GetMapping("/health")
    public ApiResponse<String> healthCheck() {
        return ApiResponse.success("Server is running!");
    }

    @Operation(summary = "에코 테스트", description = "입력한 메시지를 그대로 반환합니다.")
    @GetMapping("/echo")
    public ApiResponse<String> echo(String message) {
        return ApiResponse.success(message, "Echo completed");
    }

    @Operation(summary = "에러 테스트", description = "의도적으로 에러를 발생시킵니다.")
    @GetMapping("/error")
    public ApiResponse<Void> errorTest() {
        throw new RuntimeException("Test error!");
    }

}
