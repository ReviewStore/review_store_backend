package com.retro.domain.member.application.dto;

import jakarta.validation.constraints.NotNull;

public record MemberPublicUpdateRequest(@NotNull boolean isPublic) {

}
