package com.retro.domain.member.presentation;

import com.retro.domain.member.application.MemberService;
import com.retro.domain.member.application.dto.MemberMarketingAgreedUpdateRequest;
import com.retro.domain.member.application.dto.MemberNicknameUpdateRequest;
import com.retro.domain.member.application.dto.MemberPublicUpdateRequest;
import com.retro.domain.member.application.dto.MemberServiceTermAgreedUpdateRequest;
import com.retro.global.common.dto.ApiResponse;
import com.retro.global.common.utils.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

  private final MemberService memberService;
  private final SecurityUtil securityUtil;


  @PatchMapping("/public")
  public ApiResponse<Void> updatePostPublicStatus(
      @RequestBody @Valid MemberPublicUpdateRequest request
  ) {
    memberService.updatePostPublicStatus(securityUtil.getAuthenticatedUserId(), request.isPublic());
    return ApiResponse.success();
  }

  @PatchMapping("/nickname")
  public ApiResponse<Void> updateNickname(
      @RequestBody @Valid MemberNicknameUpdateRequest request
  ) {
    memberService.updateNickname(securityUtil.getAuthenticatedUserId(), request.nickname());
    return ApiResponse.success();
  }

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

  @DeleteMapping
  public ApiResponse<Void> withdrawMember() {
    memberService.withdrawMember(securityUtil.getAuthenticatedUserId());
    return ApiResponse.success();
  }
}
