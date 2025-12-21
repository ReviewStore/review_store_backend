package com.retro.domain.member.application.dto.request;


import lombok.Getter;

public record MemberAgreeTermsRequest(String tempMemberId, boolean marketingAgreed, String nickname) {

}
