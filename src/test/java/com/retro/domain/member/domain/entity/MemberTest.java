package com.retro.domain.member.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.retro.domain.auth.application.dto.request.AgreeTermsRequest;
import com.retro.domain.auth.application.dto.response.OAuth2AppleMemberInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MemberTest {

  private Member createMemberFixture() {
    OAuth2AppleMemberInfo oAuth2AppleMemberInfo = OAuth2AppleMemberInfo.builder()
        .sub("exValue")
        .provider(Provider.APPLE)
        .build();

    AgreeTermsRequest agreeTermsRequest = new AgreeTermsRequest(
        "exTempId",
        true,
        "retroUser"
    );

    Term term = Term.from(agreeTermsRequest.marketingAgreed());

    return Member.of(
        oAuth2AppleMemberInfo.getProvider(),
        oAuth2AppleMemberInfo.getSub(),
        agreeTermsRequest.nickname(),
        term
    );
  }

  @Test
  @DisplayName("회원 생성 시 기본값이 올바르게 초기화된다(공개=false, 역할=MEMBER, 제한열람, 잔여횟수=5)")
  void createMember_initialValues() {
    // when
    Member createdMember = createMemberFixture();

    // then
    assertThat(createdMember.getIsPublic()).isFalse();
    assertThat(createdMember.getRole()).isEqualTo(Role.MEMBER);

    assertThat(createdMember.getPostReadPermission()).isEqualTo(PostReadPermission.LIMITED);
    assertThat(createdMember.getRemainingPostReadCount()).isEqualTo(5);

    assertThat(createdMember.getTerm()).isNotNull();
    assertThat(createdMember.getTerm().getMember()).isEqualTo(createdMember);
  }

  @Test
  @DisplayName("열람 횟수를 차감하면 remainingPostReadCount가 1 감소한다")
  void reduceRemainingPostReadCount_decreaseByOne() {
    // given
    Member member = createMemberFixture();
    int before = member.getRemainingPostReadCount();

    // when
    member.reduceRemainingPostReadCount();

    // then
    assertThat(member.getRemainingPostReadCount()).isEqualTo(before - 1);
  }

  @Test
  @DisplayName("무제한 열람 권한을 부여하면 postReadPermission이 UNLIMITED로 변경된다")
  void grantPostReadPermission_setUnlimited() {
    // given
    Member member = createMemberFixture();

    // when
    member.grantPostReadPermission();

    // then
    assertThat(member.getPostReadPermission()).isEqualTo(PostReadPermission.UNLIMITED);
  }

  @Test
  @DisplayName("closePublicAccessToPost 호출 시 공개 상태는 false, 열람 권한은 LIMITED로 강제된다")
  void closePublicAccessToPost_forcePrivateAndLimited() {
    // given
    Member member = createMemberFixture();
    member.grantPostReadPermission(); // 먼저 무제한으로 만든 상태

    // when
    member.closePublicAccessToPost();

    // then
    assertThat(member.getIsPublic()).isFalse();
    assertThat(member.getPostReadPermission()).isEqualTo(PostReadPermission.LIMITED);
  }

  @Test
  @DisplayName("LIMITED 권한이면 hasLimitedPostReadPermission이 true를 반환한다")
  void hasLimitedPostReadPermission_trueWhenLimited() {
    // given
    Member member = createMemberFixture();

    // when & then
    assertThat(member.hasLimitedPostReadPermission()).isTrue();
  }

  @Test
  @DisplayName("UNLIMITED 권한이면 hasLimitedPostReadPermission이 false를 반환한다")
  void hasLimitedPostReadPermission_falseWhenUnlimited() {
    // given
    Member member = createMemberFixture();
    member.grantPostReadPermission();

    // when & then
    assertThat(member.hasLimitedPostReadPermission()).isFalse();
  }
}
