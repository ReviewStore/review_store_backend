package com.retro.domain.member.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.retro.domain.auth.application.dto.request.AgreeTermsRequest;
import com.retro.domain.auth.application.dto.response.OAuth2AppleMemberInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MemberTest {

  private Member createMemberFixture() {
    OAuth2AppleMemberInfo info = OAuth2AppleMemberInfo.builder()
        .sub("exValue")
        .provider(Provider.APPLE)
        .build();

    AgreeTermsRequest agree = new AgreeTermsRequest("exTempId", true, "retroUser");
    Term term = Term.from(agree.marketingAgreed());

    return Member.of(info.getProvider(), info.getSub(), agree.nickname(), term);
  }

  @Test
  @DisplayName("회원 생성 시 기본값이 올바르게 초기화된다(공개=false, 역할=MEMBER, 제한열람, 잔여횟수=5)")
  void createMember_initialValues() {
    Member member = createMemberFixture();

    assertThat(member.getIsPublic()).isFalse();
    assertThat(member.getRole()).isEqualTo(Role.MEMBER);
    assertThat(member.getPostReadPermission()).isEqualTo(PostReadPermission.LIMITED);
    assertThat(member.getRemainingPostReadCount()).isEqualTo(5);
    assertThat(member.getTerm()).isNotNull();
    assertThat(member.getTerm().getMember()).isEqualTo(member);
  }

  @Test
  @DisplayName("열람 횟수를 차감하면 remainingPostReadCount가 1 감소한다")
  void reduceRemainingPostReadCount_decreaseByOne() {
    Member member = createMemberFixture();
    int before = member.getRemainingPostReadCount();

    member.reduceRemainingPostReadCount();

    assertThat(member.getRemainingPostReadCount()).isEqualTo(before - 1);
  }

  @Test
  @DisplayName("무제한 열람 권한을 부여하면 postReadPermission이 UNLIMITED로 변경된다")
  void grantPostReadPermission_setUnlimited() {
    Member member = createMemberFixture();

    member.grantPostReadPermission();

    assertThat(member.getPostReadPermission()).isEqualTo(PostReadPermission.UNLIMITED);
  }
  
  @Test
  @DisplayName("LIMITED 권한이면 hasLimitedPostReadPermission이 true를 반환한다")
  void hasLimitedPostReadPermission_trueWhenLimited() {
    Member member = createMemberFixture();
    assertThat(member.hasLimitedPostReadPermission()).isTrue();
  }

  @Test
  @DisplayName("UNLIMITED 권한이면 hasLimitedPostReadPermission이 false를 반환한다")
  void hasLimitedPostReadPermission_falseWhenUnlimited() {
    Member member = createMemberFixture();
    member.grantPostReadPermission();
    assertThat(member.hasLimitedPostReadPermission()).isFalse();
  }

  @Test
  @DisplayName("openOwnPublication 호출 시 isPublic이 true가 된다")
  void openOwnPublication_setPublicTrue() {
    Member member = createMemberFixture();

    member.openOwnPublication();

    assertThat(member.getIsPublic()).isTrue();
  }

  @Test
  @DisplayName("closeOwnPublication 호출 시 isPublic이 false가 된다")
  void closeOwnPublication_setPublicTrue() {
    Member member = createMemberFixture();

    member.closeOwnPublication();

    assertThat(member.getIsPublic()).isFalse();
  }

  @Test
  @DisplayName("LIMITED + 공개(true)라면 hasLimitedPermissionAndOpenedOwnPublication은 true")
  void hasLimitedPermissionAndOpenedOwnPublication_true_whenLimitedAndPublic() {
    Member member = createMemberFixture();
    member.openOwnPublication();

    assertThat(member.hasLimitedPermissionAndOpenedOwnPublication()).isTrue();
  }

  @Test
  @DisplayName("remainingPostReadCount가 0이고 LIMITED면 isPostReadCountExceeded는 true")
  void isPostReadCountExceeded_true_whenLimitedAndZero() {
    Member member = createMemberFixture();

    for (int i = 0; i < 5; i++) {
      member.reduceRemainingPostReadCount();
    }

    assertThat(member.getRemainingPostReadCount()).isZero();
    assertThat(member.isPostReadCountExceeded()).isTrue();
  }

  @Test
  @DisplayName("remainingPostReadCount가 0이어도 UNLIMITED면 isPostReadCountExceeded는 false")
  void isPostReadCountExceeded_false_whenUnlimited() {
    Member member = createMemberFixture();
    member.grantPostReadPermission();

    assertThat(member.isPostReadCountExceeded()).isFalse();
  }
}
