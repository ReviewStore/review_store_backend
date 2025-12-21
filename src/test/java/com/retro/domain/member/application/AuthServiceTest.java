package com.retro.domain.member.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.retro.domain.member.application.dto.request.MemberAgreeTermsRequest;
import com.retro.domain.member.application.dto.response.OAuth2AppleMemberInfo;
import com.retro.domain.member.application.exception.MemberNotRegisteredException;
import com.retro.domain.member.domain.MemberRepository;
import com.retro.domain.member.domain.entity.Member;
import com.retro.domain.member.domain.entity.Provider;
import com.retro.domain.member.domain.entity.Role;
import com.retro.domain.member.domain.entity.Term;
import com.retro.global.common.exception.ErrorCode;
import com.retro.global.common.jwt.JwtProvider;
import com.retro.global.common.jwt.JwtToken;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.cache.CacheProperties.Redis;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @InjectMocks
  private AuthService authService;
  @Mock
  private AppleOAuth2Service appleOAuth2Service;
  @Mock
  private RedisService redisService;
  @Mock
  private JwtProvider jwtProvider;
  @Mock
  private MemberRepository memberRepository;

  @Test
  void testSucceedAppleLogin() {

    // given
    String authCode = "appleAuthCode123";

    OAuth2AppleMemberInfo oAuth2AppleMemberInfo = OAuth2AppleMemberInfo.builder()
        .sub("exValue")
        .provider(Provider.APPLE)
        .build();

    Term term = Term.from(true);

    Member member = Member.builder()
        .provider(Provider.APPLE)
        .providerId("exValue")
        .term(term)
        .nickname("retro1234")
        .build();

    ReflectionTestUtils.setField(member, "id", 1L);

    JwtToken jwtToken = JwtToken.builder()
        .accessToken("exAccessToken")
        .accessTokenExpiredDate(360000L)
        .refreshToken("exRefreshToken")
        .refreshTokenExpiredDate(1080000L)
        .userId(member.getId())
        .build();

    when(appleOAuth2Service.processAppleLogin(anyString())).thenReturn(oAuth2AppleMemberInfo);
    when(memberRepository.findByProviderAndProviderId(any(Provider.class), anyString())).thenReturn(
        Optional.of(member));
    when(jwtProvider.createToken(member.getId(), member.getRole().name())).thenReturn(jwtToken);

    // when
    JwtToken response = authService.appleLogin(authCode);

    // then
    assertThat(response).isNotNull();
    assertThat(response.getAccessToken()).isEqualTo(jwtToken.getAccessToken());
    assertThat(response.getRefreshToken()).isEqualTo(jwtToken.getRefreshToken());
    assertThat(response.getAccessTokenExpiredDate()).isEqualTo(jwtToken.getAccessTokenExpiredDate());
    assertThat(response.getRefreshTokenExpiredDate()).isEqualTo(jwtToken.getRefreshTokenExpiredDate());
    verify(appleOAuth2Service,times(1)).processAppleLogin(anyString());
    verify(memberRepository,times(1)).findByProviderAndProviderId(any(Provider.class), anyString());
    verify(jwtProvider,times(1)).createToken(anyLong(), anyString());
  }

  @Test
  void testAppleLoginForNotRegisteredMember() {

    // given
    String authCode = "appleAuthCode123";

    OAuth2AppleMemberInfo oAuth2AppleMemberInfo = OAuth2AppleMemberInfo.builder()
        .sub("exValue")
        .provider(Provider.APPLE)
        .build();

    when(appleOAuth2Service.processAppleLogin(anyString())).thenReturn(oAuth2AppleMemberInfo);
    when(memberRepository.findByProviderAndProviderId(any(Provider.class), anyString())).thenReturn(
        Optional.empty());
    doNothing().when(redisService).saveTempMemberInfo(anyString(), any(OAuth2AppleMemberInfo.class));

    // when & then
    assertThatThrownBy(() -> authService.appleLogin(authCode))
        .isInstanceOf(MemberNotRegisteredException.class)
        .extracting("errorCode")
        .isEqualTo(ErrorCode.MEMBER_NOT_FOUND);

    verify(appleOAuth2Service,times(1)).processAppleLogin(anyString());
    verify(memberRepository,times(1)).findByProviderAndProviderId(any(Provider.class), anyString());
    verify(redisService,times(1)).saveTempMemberInfo(anyString(), any(OAuth2AppleMemberInfo.class));

  }

  @Test
  void testRegisterTermsSuccess() {
    // given
    String tempMemberId = "temp-uuid-1234";
    MemberAgreeTermsRequest request = new MemberAgreeTermsRequest(tempMemberId, true, "retro1234");

    // Redis에 저장되어 있던 임시 회원 정보
    OAuth2AppleMemberInfo mockInfo = OAuth2AppleMemberInfo.builder()
        .sub("apple-sub-123")
        .provider(Provider.APPLE)
        .build();

    JwtToken jwtToken = JwtToken.builder()
        .accessToken("access-token")
        .refreshToken("refresh-token")
        .build();

    Term term = Term.from(true);

    Member member = Member.builder()
        .provider(Provider.APPLE)
        .providerId("exValue")
        .term(term)
        .nickname("retro1234")
        .build();

    ReflectionTestUtils.setField(member, "id", 1L);

    when(redisService.getTempMemberInfo(tempMemberId)).thenReturn(mockInfo);
    when(memberRepository.save(any(Member.class))).thenReturn(member);
    when(jwtProvider.createToken(any(), anyString())).thenReturn(jwtToken);

    // when
    JwtToken response = authService.registerTerms(request);

    // then
    assertThat(response).isNotNull();
    assertThat(response.getAccessToken()).isEqualTo("access-token");
    verify(redisService, times(1)).removeTempMemberInfo(tempMemberId);
    verify(memberRepository, times(1)).save(any(Member.class));
    verify(jwtProvider, times(1)).createToken(any(), anyString());
  }

  @Test
  void testRegisterTermsFailWhenTempMemberNotFound() {
    // given
    String tempMemberId = "invalid-uuid";
    MemberAgreeTermsRequest request = new MemberAgreeTermsRequest(tempMemberId, true, "retro1234");

    when(redisService.getTempMemberInfo(tempMemberId)).thenReturn(null);

    // when & then
    assertThatThrownBy(() -> authService.registerTerms(request))
        .isInstanceOf(NullPointerException.class);

    verify(redisService, times(1)).removeTempMemberInfo(tempMemberId);
    verify(memberRepository, times(0)).save(any(Member.class));
    verify(jwtProvider, times(0)).createToken(any(), anyString());
  }

}