package com.retro.domain.member.presentation;

import com.retro.domain.member.application.AuthService;
import com.retro.domain.member.application.dto.request.MemberAgreeTermsRequest;
import com.retro.domain.member.application.dto.response.AppleAuthCodeDto;
import com.retro.global.common.dto.ApiResponse;
import com.retro.global.common.jwt.JwtToken;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AuthService authService;

  @PostMapping("/redirect/apple")
  public ApiResponse<AppleAuthCodeDto> appleRedirect(@RequestParam Map<String, String> payload) throws Exception {
    String authCode = payload.get("code");
    AppleAuthCodeDto appleAuthCodeDto = AppleAuthCodeDto.of(authCode);
    return ApiResponse.success(appleAuthCodeDto);
  }

  @PostMapping("/oauth2/login/apple")
  public ApiResponse<JwtToken> appleLogin(@RequestParam String code) throws Exception {
    JwtToken jwtToken = authService.appleLogin(code);
    return ApiResponse.success(jwtToken);
  }

  @PostMapping("/agree-terms")
  public ApiResponse<JwtToken> registerTerms(@Validated @RequestBody MemberAgreeTermsRequest request) {
    JwtToken jwtToken = authService.registerTerms(request);
    return ApiResponse.success(jwtToken);
  }

}
