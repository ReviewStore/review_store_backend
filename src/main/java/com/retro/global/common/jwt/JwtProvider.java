package com.retro.global.common.jwt;

import com.retro.global.common.jwt.JwtToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {
  private final String ROLES = "roles";
  private final String TYPE = "tokenType";

  @Value("${spring.jwt.secret-key}")
  private String secretKey;
  @Value("${spring.jwt.access-token-expiration-ms}")
  private long accessValidTime;
  @Value("${spring.jwt.refresh-token-expiration-ms}")
  private long refreshValidTime;

  public JwtToken createToken(Long sub, String role) {
    Date now = new Date();

    // 1. Access Token용 Claims 생성 (빌더 단계에서 모든 값 추가)
    Claims accessTokenClaims = Jwts.claims()
        .subject(String.valueOf(sub))
        .add(TYPE, "access")
        .add(ROLES, role)
        .build(); // 여기서 build()를 호출하면 수정 불가능한 객체가 됨

    // 2. Refresh Token용 Claims 생성
    Claims refreshTokenClaims = Jwts.claims()
        .subject(String.valueOf(sub))
        .add(TYPE, "refresh")
        .build();

    // 3. Access Token 생성
    String accessToken = Jwts.builder()
        .claims(accessTokenClaims) // 이미 완성된 claims 주입
        .issuedAt(now)
        .expiration(new Date(now.getTime() + accessValidTime))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();

    // 4. Refresh Token 생성
    String refreshToken = Jwts.builder()
        .claims(refreshTokenClaims)
        .issuedAt(now)
        .expiration(new Date(now.getTime() + refreshValidTime))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();

    return JwtToken.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .accessTokenExpiredDate(accessValidTime)
        .refreshTokenExpiredDate(refreshValidTime)
        .userId(sub)
        .build();
  }

  private Key getSigningKey() {
    byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}