package com.retro.domain.auth.application;

import com.retro.domain.auth.application.dto.request.AgreeTermsRequest;
import com.retro.domain.auth.application.dto.response.OAuth2AppleMemberInfo;
import com.retro.domain.member.application.exception.MemberNotRegisteredException;
import com.retro.domain.member.domain.MemberRepository;
import com.retro.domain.member.domain.entity.Member;
import com.retro.domain.member.domain.entity.Term;
import com.retro.global.common.exception.ErrorCode;
import com.retro.global.common.jwt.JwtProvider;
import com.retro.global.common.jwt.JwtToken;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
  private final AppleOAuth2Service appleOAuth2Service;
  private final RedisService redisService;
  private final JwtProvider jwtProvider;
  private final MemberRepository memberRepository;

  public JwtToken appleLogin(String authCode) {
    OAuth2AppleMemberInfo appleMemberInfo = appleOAuth2Service.processAppleLogin(authCode);
    Optional<Member> memberFoundByProvider = findByProvider(appleMemberInfo);

    if (memberFoundByProvider.isPresent()) {
      Member member = memberFoundByProvider.get();
      JwtToken jwtToken = jwtProvider.createToken(member.getId(),
          member.getRole().name());
      redisService.saveMemberRefreshToken(member.getId(), jwtToken.getRefreshToken());
      return jwtToken;
    }

    String uniqueTempId = generateUniqueTempId();
    redisService.saveTempMemberInfo(uniqueTempId, appleMemberInfo);
    throw new MemberNotRegisteredException(ErrorCode.MEMBER_NOT_FOUND,
        createMessageAboutTempMemberId(uniqueTempId));
  }

  private String createMessageAboutTempMemberId(String uniqueTempId) {
    return "tempId assigned: " + uniqueTempId;
  }

  private Optional<Member> findByProvider(OAuth2AppleMemberInfo appleMemberInfo) {
    return memberRepository.findByProviderAndProviderId(
        appleMemberInfo.getProvider(), appleMemberInfo.getSub());
  }

  private String generateUniqueTempId() {
    final String prefix = "member-";
    return prefix + UUID.randomUUID();
  }

  public JwtToken registerTerms(AgreeTermsRequest request) {
    String tempMemberId = request.tempMemberId();
    OAuth2AppleMemberInfo tempMemberInfo = getTempMemberInfo(tempMemberId);
    Objects.requireNonNull(tempMemberInfo);

    Term term = Term.from(request.marketingAgreed());
    Member member = Member.of(tempMemberInfo.getProvider(), tempMemberInfo.getSub(),
        request.nickname(), term);
    memberRepository.save(member);

    JwtToken jwtToken = jwtProvider.createToken(member.getId(), member.getRole().name());
    redisService.saveMemberRefreshToken(member.getId(), jwtToken.getRefreshToken());

    return jwtToken;
  }

  private OAuth2AppleMemberInfo getTempMemberInfo(String tempMemberId) {
    OAuth2AppleMemberInfo tempMemberInfo =
        redisService.getTempMemberInfo(tempMemberId);
    redisService.removeTempMemberInfo(tempMemberId);
    return tempMemberInfo;
  }
}
