package com.retro.domain.member.application.dto;

public record MemberNicknameResponse(
    String nickname
) {

  public static MemberNicknameResponse from(String nickname) {
    return new MemberNicknameResponse(nickname);
  }
}
