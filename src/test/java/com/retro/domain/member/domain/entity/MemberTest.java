package com.retro.domain.member.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.retro.domain.auth.application.dto.request.AgreeTermsRequest;
import com.retro.domain.auth.application.dto.response.OAuth2AppleMemberInfo;
import org.junit.jupiter.api.Test;

class MemberTest {

  @Test
  void createMember() {

    // given
    OAuth2AppleMemberInfo oAuth2AppleMemberInfo = OAuth2AppleMemberInfo.builder()
        .sub("exValue")
        .provider(Provider.APPLE)
        .build();

    AgreeTermsRequest agreeTermsRequest = new AgreeTermsRequest("exTempId", true,
        "retroUser");

    Term term = Term.from(agreeTermsRequest.marketingAgreed());

    // when
    Member createdMember = Member.of(oAuth2AppleMemberInfo.getProvider(), oAuth2AppleMemberInfo.getSub(),
        agreeTermsRequest.nickname(), term);

    // then
    assertThat(createdMember.getIsPublic()).isFalse();
    assertThat(createdMember.getRole()).isEqualTo(Role.MEMBER);
    assertThat(term.getMember()).isNotNull();
    assertThat(term.getMember()).isEqualTo(createdMember);
  }

}