package com.retro.domain.member.domain.entity;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.retro.domain.member.application.dto.request.MemberAgreeTermsRequest;
import com.retro.domain.member.application.dto.response.OAuth2AppleMemberInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

class MemberTest {

  @Test
  void createMember() {

    // given
    OAuth2AppleMemberInfo oAuth2AppleMemberInfo = OAuth2AppleMemberInfo.builder()
        .sub("exValue")
        .provider(Provider.APPLE)
        .build();

    MemberAgreeTermsRequest agreeTermsRequest = new MemberAgreeTermsRequest("exTempId", true,
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