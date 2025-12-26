package com.retro.global.common.jwt;

import com.retro.domain.member.application.MemberService;
import com.retro.domain.member.domain.entity.Member;
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
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

  private final String ROLES = "roles";
  private final String TYPE = "tokenType";
  private final MemberService memberService;

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

  public Claims parseClaims(String token) {
    try {
      return Jwts.parser() // parserBuilder() 대신 parser() 사용
          .verifyWith((SecretKey) getSigningKey()) // setSigningKey 대신 verifyWith 권장 (최신 기준)
          .build()
          .parseSignedClaims(token) // parseClaimsJws 대신 parseSignedClaims 사용
          .getPayload(); // getBody() 대신 getPayload() 사용
    } catch (SecurityException e) {
      log.info("Invalid JWT signature.");
      // 두 번째 생성자를 활용해 구체적인 사유 전달
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

  public Authentication getAuthentication(String token) {
    Claims claims = parseClaims(token);
    if (isInvalidToken(claims)) {
      throw new BusinessException(ErrorCode.INVALID_TOKEN);
    }
    LoginMemberInfo LoginMemberInfo = getLoginMemberInfo(claims);
    return createUsernamePasswordAuthenticationToken(token, LoginMemberInfo);
  }

  private UsernamePasswordAuthenticationToken createUsernamePasswordAuthenticationToken(
      String token, LoginMemberInfo LoginMemberInfo) {
    return new UsernamePasswordAuthenticationToken(
        LoginMemberInfo,
        token,
        List.of(new SimpleGrantedAuthority(LoginMemberInfo.role().getCode())));
  }

  private boolean isInvalidToken(Claims claims) {
    return !claims.get(TYPE).equals("access") || claims.get(ROLES) == null;
  }

  private LoginMemberInfo getLoginMemberInfo(Claims claims) {
    String role = claims.get(ROLES).toString();
    Long id = Long.valueOf(claims.getSubject());

    if (Role.MEMBER.getCode().equals(role)) {
      Member member = memberService.findById(id);
      return LoginMemberInfo.of(member);
    } else {
      throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
    }
  }


  private Key getSigningKey() {
    byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}