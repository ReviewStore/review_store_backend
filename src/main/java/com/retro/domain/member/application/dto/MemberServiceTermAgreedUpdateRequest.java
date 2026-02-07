package com.retro.domain.member.application.dto;

import jakarta.validation.constraints.NotNull;

public record MemberServiceTermAgreedUpdateRequest(@NotNull Boolean serviceTermAgreed) {

  public static MemberServiceTermAgreedUpdateRequest of(Boolean serviceTermAgreed) {
    return new MemberServiceTermAgreedUpdateRequest(serviceTermAgreed);
  }
}
