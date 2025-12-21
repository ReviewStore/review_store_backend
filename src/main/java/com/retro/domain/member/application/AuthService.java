package com.retro.domain.member.application;

import com.retro.domain.member.application.dto.request.MemberAgreeTermsRequest;
import com.retro.domain.member.application.dto.response.OAuth2AppleMemberInfo;
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
    // 가입된 회원 없을 경우 예외처리 시키고 회원정보 레디스에 저장, 예외핸들러에서 임시 id 반환
    OAuth2AppleMemberInfo appleMemberInfo = appleOAuth2Service.processAppleLogin(authCode);
    Optional<Member> memberFoundByProvider = findByProvider(appleMemberInfo);

    if (memberFoundByProvider.isPresent()) {
      Member member = memberFoundByProvider.get();
      return jwtProvider.createToken(member.getId(),
          member.getRole().name());
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

  public JwtToken registerTerms(MemberAgreeTermsRequest request) {
    String tempMemberId = request.tempMemberId();
    OAuth2AppleMemberInfo tempMemberInfo = getTempMemberInfo(tempMemberId);
    Objects.requireNonNull(tempMemberInfo);

    Term term = Term.from(request.marketingAgreed());
    Member member = Member.of(tempMemberInfo.getProvider(), tempMemberInfo.getSub(),
        request.nickname(), term);
    memberRepository.save(member);
    return jwtProvider.createToken(member.getId(), member.getRole().name());
  }

  private OAuth2AppleMemberInfo getTempMemberInfo(String tempMemberId) {
    OAuth2AppleMemberInfo tempMemberInfo =
        redisService.getTempMemberInfo(tempMemberId);
    redisService.removeTempMemberInfo(tempMemberId);
    return tempMemberInfo;
  }
}
