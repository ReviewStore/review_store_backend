package com.retro.domain.member.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MemberNicknameUpdateRequest(
    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(min = 2, max = 12, message = "닉네임은 2자 이상 12자 이하로 입력해주세요.")
    @Pattern(regexp = "^[A-Za-z0-9가-힣]+$",
        message = "닉네임은 한글/영문/숫자만 입력 가능합니다.")
    String nickname
) {

}