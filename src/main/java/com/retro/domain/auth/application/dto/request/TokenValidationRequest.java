package com.retro.domain.auth.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "JWT 토큰 유효성 검증 요청")
public record TokenValidationRequest(

    @Schema(description = "검증할 JWT 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    @NotBlank(message = "검증할 JWT 토큰은 필수입니다.")
    String token
) {

}