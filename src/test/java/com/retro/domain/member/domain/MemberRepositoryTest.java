package com.retro.domain.member.domain;

import com.retro.domain.member.domain.entity.Member;
import com.retro.domain.member.domain.entity.Provider;
import com.retro.domain.member.domain.entity.Term;
import com.retro.domain.member.infrastructure.jpa.MemberRepositoryImpl;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(MemberRepositoryImpl.class)
class MemberRepositoryTest {

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private EntityManager em;

  @Test
  void testMemberAndTermCascadeSave() {
    // given
    Term term = Term.from(true);
    Member member = Member.of(
        Provider.APPLE,
        "apple-12345",
        "테스트유저",
        term
    );

    // when
    Member savedMember = memberRepository.save(member);

    em.flush();
    em.clear();

    Member foundMember = memberRepository.findByProviderAndProviderId(Provider.APPLE, "apple-12345")
        .orElseThrow();

    // then
    assertThat(foundMember.getNickname()).isEqualTo("테스트유저");
    assertThat(foundMember.getTerm()).isNotNull();
    assertThat(foundMember.getTerm().isMarketingAgreed()).isTrue();
    assertThat(foundMember.getTerm().isAgeOver14()).isTrue();
  }
}