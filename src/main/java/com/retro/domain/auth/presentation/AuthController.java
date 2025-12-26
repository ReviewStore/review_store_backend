package com.retro.domain.auth.presentation;

import com.retro.domain.auth.application.AuthService;
import com.retro.domain.auth.application.dto.request.AgreeTermsRequest;
import com.retro.domain.auth.application.dto.request.GoogleLoginRequest;
import com.retro.domain.auth.application.dto.request.RefreshRequest;
import com.retro.domain.auth.application.dto.response.AppleAuthCodeDto;
import com.retro.global.common.dto.ApiResponse;
import com.retro.global.common.jwt.JwtToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.StreamingHttpOutputMessage.Body;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth API", description = "인증 및 소셜 로그인 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController {

  private final AuthService authService;

  @Operation(
      summary = "애플 로그인 리디렉션 수신",
      description = "애플 인증 서버에서 전송하는 콜백을 수신합니다. 애플 서버 전용 엔드포인트입니다."
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공",
      content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
          examples = @ExampleObject(value = """
                {
                  "status": 200,
                  "message": "SUCCESS",
                  "data": {
                    "code": "apple_auth_code_sample"
                  }
                }
                """)))
  @PostMapping(value = "/redirect/apple", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public ApiResponse<AppleAuthCodeDto> appleRedirect(
      @Parameter(hidden = true) @RequestParam Map<String, String> payload
  ) throws Exception {
    String authCode = payload.get("code");
    return ApiResponse.success(AppleAuthCodeDto.of(authCode));
  }

  @Operation(summary = "애플 로그인 처리", description = "인증 코드로 JWT 토큰을 발급합니다.")
  @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공",
      content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
          examples = @ExampleObject(value = """
                {
                  "status": 200,
                  "message": "SUCCESS",
                  "data": {
                    "accessToken": "eyJhbGciOiJIUzI1...",
                    "refreshToken": "eyJhbGciOiJIUzI1...",
                    "accessTokenExpiredDate": 1735000000000,
                    "refreshTokenExpiredDate": 1736000000000,
                    "userId": 42
                  }
                }
                """)))
  @PostMapping("/oauth2/login/apple")
  public ApiResponse<JwtToken> appleLogin(
      @Parameter(description = "애플 인증 코드", example = "c7e.apple.auth.code")
      @RequestParam String code
  ) throws Exception {
    return ApiResponse.success(authService.appleLogin(code));
  }

  @Operation(summary = "구글 로그인", description = "Google ID Token으로 JWT 토큰을 발급합니다.")
  @PostMapping("/oauth2/login/google")
  public ApiResponse<JwtToken> googleLogin(@RequestBody GoogleLoginRequest request) {
    JwtToken jwtToken = authService.googleLogin(request.getIdToken());
    return ApiResponse.success(jwtToken);
  }

  @Operation(summary = "약관 동의 및 회원가입 완료", description = "임시 ID로 약관 동의 후 최종 토큰을 발급합니다.")
  @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공",
      content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
          examples = @ExampleObject(value = """
                {
                  "status": 200,
                  "message": "SUCCESS",
                  "data": {
                    "accessToken": "new_access_token",
                    "refreshToken": "new_refresh_token",
                    "accessTokenExpiredDate": 1735000000000,
                    "refreshTokenExpiredDate": 1736000000000,
                    "userId": 42
                  }
                }
                """)))
  @PostMapping("/agree-terms")
  public ApiResponse<JwtToken> registerTerms(
      @RequestBody()
      @Validated @io.swagger.v3.oas.annotations.parameters.RequestBody AgreeTermsRequest request
  ) {
    return ApiResponse.success(authService.registerTerms(request));
  }

  @Operation(
      summary = "JWT 토큰 재발급", 
      description = "Refresh Token으로 Access Token과 Refresh Token을 모두 재발급합니다. (Sliding Session)"
  )
  @PostMapping("/refresh")
  public ApiResponse<JwtToken> refresh(
      @RequestBody @Validated RefreshRequest request
  ) {
    JwtToken newToken = authService.refresh(request.refreshToken());
    return ApiResponse.success(newToken);
  }
}