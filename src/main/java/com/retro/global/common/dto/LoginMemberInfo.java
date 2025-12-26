package com.retro.global.common.dto;

import com.retro.domain.member.domain.entity.Role;
import lombok.Builder;

/**
 * JWT 인증에 사용되는 로그인 회원 정보
 * SecurityContext에 저장되어 @AuthenticationPrincipal로 주입 가능
 */
@Builder
public record LoginMemberInfo(
    Long id,
    Role role
) {
}
