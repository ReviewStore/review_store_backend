package com.retro.domain.member.domain.entity;

import com.retro.global.common.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(
    name = "members",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_member_provider",
            columnNames = {"provider", "provider_id"}) // DB 컬럼명 기준으로 변경
    })
public class Member extends BaseEntity {

  public static final Long DELETED_MEMBER_ID = -1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Provider provider;

  @Column(nullable = false)
  private String providerId;

  @Column(nullable = false)
  private Boolean isPublic;

  @Column(nullable = false)
  private String nickname;

  @Enumerated(EnumType.STRING)
  private Role role;

  @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
  private Term term;

  @Enumerated(EnumType.STRING)
  private PostReadPermission postReadPermission;

  @Column(nullable = false)
  private int remainingPostReadCount;

  @Builder(access = AccessLevel.PRIVATE)
  private Member(Provider provider, String providerId, String nickname, Term term) {
    this.provider = provider;
    this.providerId = providerId;
    this.nickname = nickname;
    this.term = term;
    this.role = Role.MEMBER;
    this.isPublic = false;
    this.postReadPermission = PostReadPermission.LIMITED;
    this.remainingPostReadCount = 5;
  }

  public static Member of(Provider provider, String providerId, String nickname, Term term) {
    Member member = Member.builder()
        .provider(provider)
        .providerId(providerId)
        .nickname(nickname)
        .term(term)
        .build();
    member.addTerm(term);
    return member;
  }

  public void addTerm(Term term) {
    this.term = term;
    term.addMember(this);
  }

  public void reduceRemainingPostReadCount() {
    this.remainingPostReadCount--;
  }

  public void grantPostReadPermission() {
    this.postReadPermission = PostReadPermission.UNLIMITED;
  }

  public boolean hasLimitedPostReadPermission() {
    return this.postReadPermission.equals(PostReadPermission.LIMITED);
  }

  public void openOwnPublication() {
    this.isPublic = true;
  }

  public void closeOwnPublication() {
    this.isPublic = false;
    if (this.postReadPermission.equals(PostReadPermission.UNLIMITED)) {
      this.postReadPermission = PostReadPermission.LIMITED;
    }
  }

  public boolean hasLimitedPermissionAndOpenedOwnPublication() {
    return this.hasLimitedPostReadPermission()
        && this.isPublic;
  }

  public boolean isPostReadCountExceeded() {
    return this.postReadPermission.equals(PostReadPermission.LIMITED)
        && this.remainingPostReadCount == 0;
  }

  public void updateNickname(String nickname) {
    this.nickname = nickname;
  }

  public void updateMarketingTermAgreed(
      boolean marketingTermAgreed) {
    this.term.updateMarketingAgreed(marketingTermAgreed);
  }

  public void updateServiceTermAgreed(
      boolean serviceTermAgreed) {
    this.term.updateServiceTermAgreed(serviceTermAgreed);
  }


}