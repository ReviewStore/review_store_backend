package com.retro.global.common.jwt;

import com.retro.domain.member.domain.entity.Role;
import com.retro.global.common.dto.LoginMemberInfo;
import com.retro.global.common.exception.BusinessException;
import com.retro.global.common.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtProvider {
  
  private static final String ROLES = "roles";
  private static final String TYPE = "tokenType";

  @Value("${spring.jwt.secret-key}")
  private String secretKey;
  
  @Value("${spring.jwt.access-token-expiration-ms}")
  private long accessValidTime;
  
  @Value("${spring.jwt.refresh-token-expiration-ms}")
  private long refreshValidTime;

  /**
   * 토큰 상태를 나타내는 열거형
   */
  public enum TokenStatus {
    VALID,      // 유효한 토큰
    EXPIRED,    // 만료된 토큰
    INVALID     // 유효하지 않은 토큰 (서명 불일치, 형식 오류 등)
  }

  /**
   * JWT 토큰 생성
   */
  public JwtToken createToken(Long sub, String roleCode) {
    Date now = new Date();

    // Access Token Claims
    Claims accessTokenClaims = Jwts.claims()
        .subject(String.valueOf(sub))
        .add(TYPE, "access")
        .add(ROLES, roleCode)
        .build();

    // Refresh Token Claims
    Claims refreshTokenClaims = Jwts.claims()
        .subject(String.valueOf(sub))
        .add(TYPE, "refresh")
        .build();

    // Access Token 생성
    String accessToken = Jwts.builder()
        .claims(accessTokenClaims)
        .issuedAt(now)
        .expiration(new Date(now.getTime() + accessValidTime))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();

    // Refresh Token 생성
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

  /**
   * JWT 토큰에서 Authentication 객체 생성
   * 필터에서 SecurityContext에 저장할 인증 정보 생성
   */
  public Authentication getAuthentication(String token) {
    Claims claims = parseClaims(token);
    
    // 토큰 타입 검증 (access 토큰인지 확인)
    if (isInvalidAccessToken(claims)) {
      throw new BusinessException(ErrorCode.INVALID_TOKEN, "Access Token이 아니거나 필수 정보가 없습니다.");
    }
    
    // JWT claims에서 직접 정보 추출 (DB 조회 없이!)
    Long userId = Long.valueOf(claims.getSubject());
    String roleCode = claims.get(ROLES, String.class);
    Role role = Role.fromCode(roleCode);
    
    // LoginMemberInfo 생성
    LoginMemberInfo loginMemberInfo = LoginMemberInfo.builder()
        .id(userId)
        .role(role)
        .build();
    
    // Authentication 객체 생성
    return new UsernamePasswordAuthenticationToken(
        loginMemberInfo,  // principal
        token,            // credentials
        List.of(new SimpleGrantedAuthority(roleCode))  // authorities
    );
  }

  /**
   * 토큰 상태 확인 (유효, 만료, 유효하지 않음)
   */
  public TokenStatus checkToken(String token) {
    try {
      Jwts.parser()
          .verifyWith((SecretKey) getSigningKey())
          .build()
          .parseSignedClaims(token);
      return TokenStatus.VALID;
    } catch (ExpiredJwtException e) {
      log.debug("만료된 JWT 토큰: {}", e.getMessage());
      return TokenStatus.EXPIRED;
    } catch (UnsupportedJwtException | MalformedJwtException | 
             SignatureException | IllegalArgumentException e) {
      log.debug("유효하지 않은 JWT 토큰: {}", e.getMessage());
      return TokenStatus.INVALID;
    }
  }

  /**
   * JWT 토큰 파싱 및 Claims 추출
   */
  public Claims parseClaims(String token) {
    try {
      return Jwts.parser()
          .verifyWith((SecretKey) getSigningKey())
          .build()
          .parseSignedClaims(token)
          .getPayload();
    } catch (SecurityException e) {
      log.info("Invalid JWT signature.");
      throw new BusinessException(ErrorCode.INVALID_TOKEN, "잘못된 JWT 서명입니다.");
    } catch (MalformedJwtException e) {
      log.info("Invalid JWT token.");
      throw new BusinessException(ErrorCode.INVALID_TOKEN, "유효하지 않은 구성의 JWT 토큰입니다.");
    } catch (ExpiredJwtException e) {
      log.info("Expired JWT token.");
      throw new BusinessException(ErrorCode.INVALID_TOKEN, "만료된 JWT 토큰입니다.");
    } catch (UnsupportedJwtException e) {
      log.info("Unsupported JWT token.");
      throw new BusinessException(ErrorCode.INVALID_TOKEN, "지원되지 않는 형식의 JWT 토큰입니다.");
    } catch (IllegalArgumentException e) {
      log.info("JWT token compact of handler are invalid.");
      throw new BusinessException(ErrorCode.INVALID_TOKEN, "JWT 핸들러 데이터가 잘못되었습니다.");
    }
  }

  /**
   * Access Token 유효성 검증
   * - tokenType이 "access"인지 확인
   * - roles 정보가 있는지 확인
   */
  private boolean isInvalidAccessToken(Claims claims) {
    return !claims.get(TYPE).equals("access") || claims.get(ROLES) == null;
  }

  /**
   * JWT 토큰 검증 (유효성만 확인) - 하위 호환성 유지
   */
  public boolean validateToken(String token) {
    return checkToken(token) == TokenStatus.VALID;
  }

  /**
   * JWT에서 사용자 ID 추출
   */
  public Long getUserId(String token) {
    Claims claims = parseClaims(token);
    return Long.parseLong(claims.getSubject());
  }

  /**
   * JWT에서 역할(Role) 추출
   */
  public String getRole(String token) {
    Claims claims = parseClaims(token);
    return claims.get(ROLES, String.class);
  }

  /**
   * 서명 키 생성
   */
  private Key getSigningKey() {
    byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
