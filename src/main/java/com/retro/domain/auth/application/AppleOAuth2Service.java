package com.retro.domain.auth.application;

import com.retro.domain.auth.application.dto.response.ApplePublicKeyDto;
import com.retro.domain.auth.application.dto.response.ApplePublicKeyDto.ApplePublicKey;
import com.retro.domain.auth.application.dto.response.AppleTokenResponseDto;
import com.retro.domain.auth.application.dto.response.OAuth2AppleMemberInfo;
import com.retro.domain.member.domain.entity.Provider;
import com.retro.global.common.dto.MemberDevice;
import com.retro.global.common.enums.DeviceType;
import com.retro.global.common.jwt.MyKeyLocator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppleOAuth2Service {

  private static final long THIRTY_DAYS_MS = 30L * 24 * 60 * 60 * 1000;
  private static final String BASE_URL = "https://appleid.apple.com";
  @Value("${spring.oauth2.apple.client-id-app}")
  private String clientIdApp;
  @Value("${spring.oauth2.apple.client-id-web}")
  private String clientIdWeb;
  @Value("${spring.oauth2.apple.private-key}")
  private String privateKey;
  @Value("${spring.oauth2.apple.key-id}")
  private String keyId;
  @Value("${spring.oauth2.apple.team-id}")
  private String teamId;

  public OAuth2AppleMemberInfo processAppleLogin(String authCode, MemberDevice memberDevice) {
    AppleTokenResponseDto appleTokenResponseDto = getAppleTokenResponse(authCode, memberDevice);
    return verifyIdToken(appleTokenResponseDto.getIdToken());
  }

  private AppleTokenResponseDto getAppleTokenResponse(String authCode, MemberDevice memberDevice) {
    String clientId =
        memberDevice.getDeviceType().equals(DeviceType.APP) ? clientIdApp : clientIdWeb;

    WebClient webClient = WebClient.builder()
        .baseUrl(BASE_URL)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8")
        .build();

    try {
      return webClient.post()
          .uri(uriBuilder -> uriBuilder.path("/auth/token")
              .queryParam("grant_type", "authorization_code")
              .queryParam("client_id", clientId)
              .queryParam("client_secret", makeClientSecretToken(clientId))
              .queryParam("code", authCode)
              .build())
          .retrieve()
          .bodyToMono(AppleTokenResponseDto.class)
          .block();
    } catch (WebClientResponseException e) {
      log.error("[애플 로그인 실패]: " + e.getResponseBodyAsString(), e);
      throw e;
    }
  }

  private OAuth2AppleMemberInfo verifyIdToken(String idToken) {
    MyKeyLocator myKeyLocator = new MyKeyLocator(getPublicKeys());
    Claims claims = parswJwt(myKeyLocator, idToken);
    return OAuth2AppleMemberInfo.builder()
        .sub(claims.get("sub", String.class))
        .provider(Provider.APPLE)
        .build();

  }

  private Claims parswJwt(MyKeyLocator myKeyLocator, String idToken) {
    Claims claims = Jwts.parser()
        .keyLocator(myKeyLocator)
        .build()
        .parseSignedClaims(idToken)
        .getPayload();
    return claims;
  }

  private List<ApplePublicKey> getPublicKeys() {
    WebClient webClient = WebClient.builder()
        .baseUrl(BASE_URL)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8")
        .build();

    ApplePublicKeyDto response = webClient.get()
        .uri("/auth/keys") // baseUrl 뒤에 붙는 상세 경로
        .retrieve()
        .bodyToMono(ApplePublicKeyDto.class)
        .block();

    return Objects.requireNonNull(response).getKeys();

  }

  private String makeClientSecretToken(String clientId) {
    String token = Jwts.builder()
        .subject(clientId) // sub
        .issuer(teamId) // iss
        .issuedAt(new Date()) // iat
        .expiration(new Date(System.currentTimeMillis() + THIRTY_DAYS_MS)) // exp
        .audience() // aud
        .add("https://appleid.apple.com")
        .and()
        .header()
        .keyId(keyId)
        .and()
        .signWith(getPrivateKey(), Jwts.SIG.ES256)
        .compact();
    return token;
  }

  private PrivateKey getPrivateKey() {
    try {
      byte[] privateKeyBytes = Decoders.BASE64.decode(privateKey);
      PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
      KeyFactory keyFactory = KeyFactory.getInstance("EC");
      return keyFactory.generatePrivate(keySpec);
    } catch (Exception e) {
      log.info("[애플 로그인] PK 생성 실패", e);
      throw new RuntimeException("애플 로그인 실패");
    }
  }
}
