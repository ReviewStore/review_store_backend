package com.retro.domain.member.application.dto;

import jakarta.validation.constraints.NotNull;

public record MemberMarketingAgreedUpdateRequest(@NotNull Boolean marketingAgreed) {

  public static MemberMarketingAgreedUpdateRequest of(Boolean marketingAgreed) {
    return new MemberMarketingAgreedUpdateRequest(marketingAgreed);
  }
}
