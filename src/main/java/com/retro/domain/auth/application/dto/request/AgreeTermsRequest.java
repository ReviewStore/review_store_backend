package com.retro.domain.auth.application.dto.request;


public record AgreeTermsRequest(String tempMemberId, boolean marketingAgreed, String nickname) {

}
