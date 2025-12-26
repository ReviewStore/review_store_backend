package com.retro.domain.auth.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "구글 로그인 요청")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleLoginRequest {

    @Schema(description = "구글 ID Token", example = "eyJhbGciOiJSUzI1NiIsImtpZCI6...")
    private String idToken;
}