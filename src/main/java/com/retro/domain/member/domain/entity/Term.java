package com.retro.domain.member.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Term {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  private boolean isAgeOver14;

  private boolean serviceTermAgreed;

  private boolean privacyPolicyAgreed;

  private boolean marketingAgreed;

  public static Term from(boolean marketingAgreed) {
    Term term = new Term();
    term.isAgeOver14 = true;
    term.serviceTermAgreed = true;
    term.privacyPolicyAgreed = true;
    term.marketingAgreed = marketingAgreed;
    return term;
  }

  public void addMember(Member member) {
    this.member = member;
  }

  public void updateMarketingAgreed(boolean marketingAgreed) {
    this.marketingAgreed = marketingAgreed;
  }

  public void updateServiceTermAgreed(boolean serviceTermAgreed) {
    this.serviceTermAgreed = serviceTermAgreed;
  }
}
