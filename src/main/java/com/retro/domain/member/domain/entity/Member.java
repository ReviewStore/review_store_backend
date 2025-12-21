package com.retro.domain.member.domain.entity;

import com.retro.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Builder.ObtainVia;
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

  public void addTerm(Term term) {
    this.term = term;
    term.addMember(this);
  }

  @Builder
  private Member(Provider provider, String providerId, String nickname, Term term) {
    this.provider = provider;
    this.providerId = providerId;
    this.nickname = nickname;
    this.term = term;
    this.role = Role.MEMBER;
    this.isPublic = false;
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
}