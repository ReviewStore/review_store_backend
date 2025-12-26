package com.retro.domain.auth.application;

import com.retro.domain.auth.application.dto.request.AgreeTermsRequest;
import com.retro.domain.auth.application.dto.response.OAuth2AppleMemberInfo;
import com.retro.domain.auth.application.dto.response.OAuth2GoogleMemberInfo;
import com.retro.domain.member.application.exception.MemberNotRegisteredException;
import com.retro.domain.member.domain.MemberRepository;
import com.retro.domain.member.domain.entity.Member;
import com.retro.domain.member.domain.entity.Provider;
import com.retro.domain.member.domain.entity.Term;
import com.retro.global.common.exception.BusinessException;
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
  private final GoogleOAuth2Service googleOAuth2Service;
  private final RedisService redisService;
  private final JwtProvider jwtProvider;
  private final MemberRepository memberRepository;

  public JwtToken appleLogin(String authCode) {
    OAuth2AppleMemberInfo appleMemberInfo = appleOAuth2Service.processAppleLogin(authCode);
    Optional<Member> memberFoundByProvider = findByProvider(appleMemberInfo);

    if (memberFoundByProvider.isPresent()) {
      Member member = memberFoundByProvider.get();
      JwtToken jwtToken = jwtProvider.createToken(member.getId(),
          member.getRole().getCode());
      redisService.saveMemberRefreshToken(member.getId(), jwtToken.getRefreshToken());
      return jwtToken;
    }

    String uniqueTempId = generateUniqueTempId();
    redisService.saveTempMemberInfo(uniqueTempId, appleMemberInfo);
    throw new MemberNotRegisteredException(ErrorCode.TERMS_AGREEMENT_REQUIRED,
        createMessageAboutTempMemberId(uniqueTempId));
  }

  public JwtToken googleLogin(String idToken) {
    // 1. ID Token 검증 및 사용자 정보 추출
    OAuth2GoogleMemberInfo googleMemberInfo = googleOAuth2Service.processGoogleLogin(idToken);

    // 2. DB에서 회원 조회
    Optional<Member> existingMember = findByProvider(
        googleMemberInfo.getProvider(),
        googleMemberInfo.getSub()
    );

    // 3. 기존 회원이면 JWT 발급
    if (existingMember.isPresent()) {
      Member member = existingMember.get();
      JwtToken jwtToken = jwtProvider.createToken(
          member.getId(),
          member.getRole().getCode()
      );

      redisService.saveMemberRefreshToken(member.getId(), jwtToken.getRefreshToken());
      return jwtToken;
    }

    // 4. 신규 회원이면 Redis 임시 저장 후 예외
    String tempId = generateUniqueTempId();
    redisService.saveTempMemberInfo(tempId, googleMemberInfo);

    throw new MemberNotRegisteredException(ErrorCode.TERMS_AGREEMENT_REQUIRED,
        tempId
    );
  }

  private Optional<Member> findByProvider(Provider provider, String providerId) {
    return memberRepository.findByProviderAndProviderId(provider, providerId);
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
    Object tempMemberInfo = getTempMemberInfo(tempMemberId);
    Objects.requireNonNull(tempMemberInfo, "임시 회원 정보가 없습니다.");

    // Apple 또는 Google 정보 처리
    Provider provider;
    String providerId;

    if (tempMemberInfo instanceof OAuth2AppleMemberInfo appleMemberInfo) {
      provider = appleMemberInfo.getProvider();
      providerId = appleMemberInfo.getSub();
    } else if (tempMemberInfo instanceof OAuth2GoogleMemberInfo googleMemberInfo) {
      provider = googleMemberInfo.getProvider();
      providerId = googleMemberInfo.getSub();
    } else {
      throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
    }

    Term term = Term.from(request.marketingAgreed());
    Member member = Member.of(provider, providerId, request.nickname(), term);
    memberRepository.save(member);

    JwtToken jwtToken = jwtProvider.createToken(member.getId(), member.getRole().getCode());
    redisService.saveMemberRefreshToken(member.getId(), jwtToken.getRefreshToken());

    return jwtToken;
  }

  private Object getTempMemberInfo(String tempMemberId) {
    Object tempMemberInfo = redisService.getTempMemberInfo(tempMemberId);
    redisService.removeTempMemberInfo(tempMemberId);
    return tempMemberInfo;
  }

  /**
   * JWT 토큰 재발급 (Provider 구분 없음 - 공통 처리)
   */
  public JwtToken refresh(String refreshToken) {
    // 1. Refresh Token 검증
    if (!jwtProvider.validateToken(refreshToken)) {
      throw new BusinessException(ErrorCode.INVALID_TOKEN);
    }

    // 2. Refresh Token에서 userId 추출
    Long userId = jwtProvider.getUserId(refreshToken);

    // 3. Redis에 저장된 RefreshToken과 비교
    String savedToken = redisService.getMemberRefreshToken(userId);
    if (savedToken == null || !refreshToken.equals(savedToken)) {
      throw new BusinessException(ErrorCode.INVALID_TOKEN);
    }

    // 4. Member 조회
    Member member = memberRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

    // 5. 새 JWT 토큰 발급 (Access + Refresh 모두 갱신 - Sliding Session)
    JwtToken newToken = jwtProvider.createToken(userId, member.getRole().getCode());

    // 6. Redis에 새 RefreshToken 저장
    redisService.saveMemberRefreshToken(userId, newToken.getRefreshToken());

    log.info("JWT 토큰 재발급 완료 - userId: {}", userId);
    return newToken;
  }
}
