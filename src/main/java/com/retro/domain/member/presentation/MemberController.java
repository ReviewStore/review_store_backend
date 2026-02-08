package com.retro.domain.member.presentation;

import com.retro.domain.member.application.MemberService;
import com.retro.domain.member.application.dto.MemberMarketingAgreedUpdateRequest;
import com.retro.domain.member.application.dto.MemberNicknameUpdateRequest;
import com.retro.domain.member.application.dto.MemberPublicUpdateRequest;
import com.retro.domain.member.application.dto.MemberServiceTermAgreedUpdateRequest;
import com.retro.global.common.dto.ApiResponse;
import com.retro.global.common.utils.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Member API", description = "회원 정보 관리 API")
@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

  private final MemberService memberService;
  private final SecurityUtil securityUtil;

  @Operation(
      summary = "공개 여부 수정",
      description = "회원의 회고 공개 여부를 수정합니다."
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "200",
      description = "성공",
      content = @io.swagger.v3.oas.annotations.media.Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE
      )
  )
  @PatchMapping("/public")
  public ApiResponse<Void> updatePostPublicStatus(
      @RequestBody @Valid MemberPublicUpdateRequest request
  ) {
    memberService.updatePostPublicStatus(securityUtil.getAuthenticatedUserId(), request.isPublic());
    return ApiResponse.success();
  }

  @Operation(
      summary = "닉네임 수정",
      description = "회원의 닉네임을 수정합니다."
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "200",
      description = "성공",
      content = @io.swagger.v3.oas.annotations.media.Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE
      )
  )
  @PatchMapping("/nickname")
  public ApiResponse<Void> updateNickname(
      @RequestBody @Valid MemberNicknameUpdateRequest request
  ) {
    memberService.updateNickname(securityUtil.getAuthenticatedUserId(), request.nickname());
    return ApiResponse.success();
  }

  @Operation(
      summary = "서비스 약관 동의 갱신",
      description = "회원의 서비스 약관 동의 여부를 수정합니다."
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "200",
      description = "성공",
      content = @io.swagger.v3.oas.annotations.media.Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE
      )
  )
  @PatchMapping("/service-term-agreed")
  public ApiResponse<Void> updateServiceTermAgreed(
      @RequestBody @Valid MemberServiceTermAgreedUpdateRequest request
  ) {
    memberService.updateServiceTermAgreed(
        securityUtil.getAuthenticatedUserId(),
        request.serviceTermAgreed()
    );
    return ApiResponse.success();
  }

  @Operation(
      summary = "마케팅 수신 동의 갱신",
      description = "회원의 마케팅 수신 동의 여부를 수정합니다."
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "200",
      description = "성공",
      content = @io.swagger.v3.oas.annotations.media.Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE
      )
  )
  @PatchMapping("/marketing-agreed")
  public ApiResponse<Void> updateMarketingAgreed(
      @RequestBody @Valid MemberMarketingAgreedUpdateRequest request
  ) {
    memberService.updateMarketingAgreed(
        securityUtil.getAuthenticatedUserId(),
        request.marketingAgreed()
    );
    return ApiResponse.success();
  }

  @Operation(
      summary = "회원 탈퇴",
      description = "회원 계정을 탈퇴 처리합니다."
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "200",
      description = "성공",
      content = @io.swagger.v3.oas.annotations.media.Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE
      )
  )
  @DeleteMapping
  public ApiResponse<Void> withdrawMember() {
    memberService.withdrawMember(securityUtil.getAuthenticatedUserId());
    return ApiResponse.success();
  }
}
