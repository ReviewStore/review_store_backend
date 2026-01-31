package com.retro.domain.member.domain;

import com.retro.domain.member.domain.entity.Member;
import com.retro.domain.member.domain.entity.Provider;
import java.util.Optional;

public interface MemberRepository {
    Member save(Member member);
    Optional<Member> findById(Long id);
    boolean existsByProviderAndProviderId(Provider provider, String providerId);
    Optional<Member> findByProviderAndProviderId(Provider provider, String providerId);
}
