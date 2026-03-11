package com.retro.domain.auth.application;

import com.retro.domain.auth.application.dto.request.GoogleClientPlatform;
import com.retro.domain.auth.application.dto.response.OAuth2GoogleMemberInfo;
import com.retro.domain.member.domain.entity.Provider;
import com.retro.global.common.exception.BusinessException;
import com.retro.global.common.exception.ErrorCode;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoogleOAuth2Service {

  private static final String GOOGLE_TOKEN_INFO_URL = "https://oauth2.googleapis.com/tokeninfo?id_token=";
  private final WebClient webClient;
  @Value("${spring.oauth2.google.client-id-ios}")
  private String clientIdIos;

  @Value("${spring.oauth2.google.client-id-web}")
  private String clientIdWeb;

  @Value("${spring.oauth2.google.client-id-android}")
  private String clientIdAndroid;

  public OAuth2GoogleMemberInfo processGoogleLogin(String idToken, GoogleClientPlatform platform) {
    try {
      // 1. Google tokeninfo API로 ID Token 검증
      Map<String, Object> tokenInfo = verifyIdTokenWithGoogle(idToken);

      // 2. Client ID 검증 (보안)
      String aud = (String) tokenInfo.get("aud");
      String expectedClientId = getClientIdByPlatform(platform);
      if (!expectedClientId.equals(aud)) {
        throw new BusinessException(ErrorCode.INVALID_TOKEN);
      }

      // 3. 사용자 정보 추출
      return extractUserInfo(tokenInfo);

    } catch (WebClientResponseException e) {
      throw new BusinessException(ErrorCode.OAUTH_AUTHENTICATION_FAILED);
    } catch (BusinessException e) {
      throw e;
    } catch (Exception e) {
      throw new BusinessException(ErrorCode.OAUTH_AUTHENTICATION_FAILED);
    }
  }

  private String getClientIdByPlatform(GoogleClientPlatform platform) {
    if (Objects.isNull(platform)) {
      throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
    }
    return switch (platform) {
      case IOS -> clientIdIos;
      case WEB -> clientIdWeb;
      case ANDROID -> clientIdAndroid;
    };
  }

  private Map<String, Object> verifyIdTokenWithGoogle(String idToken) {
    Map<String, Object> tokenInfo = webClient.get()
        .uri(GOOGLE_TOKEN_INFO_URL + idToken)
        .retrieve()
        .bodyToMono(Map.class)
        .block();

    if (tokenInfo == null || tokenInfo.isEmpty()) {
      throw new BusinessException(ErrorCode.INVALID_TOKEN);
    }

    return tokenInfo;
  }

  private OAuth2GoogleMemberInfo extractUserInfo(Map<String, Object> tokenInfo) {
    String sub = (String) tokenInfo.get("sub");
    String email = (String) tokenInfo.get("email");
    String name = (String) tokenInfo.get("name");
    String picture = (String) tokenInfo.get("picture");

    return OAuth2GoogleMemberInfo.builder()
        .sub(sub)
        .email(email)
        .name(name != null ? name : email)  // name이 없으면 email 사용
        .picture(picture)
        .provider(Provider.GOOGLE)
        .build();
  }
}