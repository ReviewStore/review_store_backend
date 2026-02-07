package com.retro.domain.member.infrastructure;

import com.retro.domain.member.domain.MemberRepository;
import com.retro.domain.member.domain.entity.Member;
import com.retro.domain.member.domain.entity.Provider;
import com.retro.domain.member.infrastructure.jpa.MemberJPARepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

  private final MemberJPARepository memberJPARepository;

  @Override
  public Member save(Member member) {
    return memberJPARepository.save(member);
  }

  @Override
  public Optional<Member> findById(Long id) {
    return memberJPARepository.findById(id);
  }

  @Override
  public boolean existsByProviderAndProviderId(Provider provider, String providerId) {
    return memberJPARepository.existsByProviderAndProviderId(provider, providerId);
  }

  @Override
  public Optional<Member> findByProviderAndProviderId(Provider provider, String providerId) {
    return memberJPARepository.findByProviderAndProviderId(provider, providerId);
  }

  @Override
  public void delete(Member member) {
    memberJPARepository.delete(member);
  }
}
