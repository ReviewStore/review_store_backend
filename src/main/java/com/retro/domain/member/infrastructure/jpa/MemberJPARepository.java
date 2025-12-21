package com.retro.domain.member.infrastructure.jpa;

import com.retro.domain.member.domain.entity.Member;
import com.retro.domain.member.domain.entity.Provider;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberJPARepository extends JpaRepository<Member, Long> {

  boolean existsByProviderAndProviderId(Provider provider, String providerId);

  Optional<Member> findByProviderAndProviderId(Provider provider, String providerId);
}
