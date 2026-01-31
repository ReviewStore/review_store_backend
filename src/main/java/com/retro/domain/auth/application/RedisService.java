package com.retro.domain.auth.application;

import com.retro.domain.auth.application.dto.response.OAuth2AppleMemberInfo;
import com.retro.domain.auth.application.dto.response.OAuth2GoogleMemberInfo;
import java.time.Duration;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisService {

  private static final String REFRESH_TOKEN_PREFIX = "rt:";
  private static final String PROVIDER_TYPE_KEY = "providerType:";

  private final RedisTemplate<String, String> redisStringTemplate;
  
  @Value("${spring.jwt.register-valid-time}")
  private long validTimeForRegistration;
  
  @Value("${spring.jwt.refresh-token-expiration-ms}")
  private long refreshTokenValidTime;

  public void saveTempMemberInfo(String uniqueTempId, OAuth2AppleMemberInfo memberInfo) {
    Map<String, Object> map = memberInfo.toMap();
    map.put(PROVIDER_TYPE_KEY, "APPLE");
    redisStringTemplate.opsForHash().putAll(uniqueTempId, map);
    redisStringTemplate.expire(uniqueTempId, Duration.ofMillis(validTimeForRegistration));
  }

  public void saveTempMemberInfo(String uniqueTempId, OAuth2GoogleMemberInfo memberInfo) {
    Map<String, Object> map = memberInfo.toMap();
    map.put(PROVIDER_TYPE_KEY, "GOOGLE");
    redisStringTemplate.opsForHash().putAll(uniqueTempId, map);
    redisStringTemplate.expire(uniqueTempId, Duration.ofMillis(validTimeForRegistration));
  }

  public Object getTempMemberInfo(String key) {
    Map<Object, Object> map = redisStringTemplate.opsForHash().entries(key);
    
    if (map.isEmpty()) {
      return null;
    }
    
    String providerType = (String) map.get(PROVIDER_TYPE_KEY);
    
    if ("GOOGLE".equals(providerType)) {
      return OAuth2GoogleMemberInfo.fromMap(map);
    } else {
      return OAuth2AppleMemberInfo.fromMap(map);
    }
  }

  public void removeTempMemberInfo(String key) {
    redisStringTemplate.delete(key);
  }

  public void saveMemberRefreshToken(Long memberId, String refreshToken) {
    String key = REFRESH_TOKEN_PREFIX + memberId;
    redisStringTemplate.opsForValue().set(key, refreshToken);
    redisStringTemplate.expire(key, Duration.ofMillis(refreshTokenValidTime));
  }

  public String getMemberRefreshToken(Long memberId) {
    String key = REFRESH_TOKEN_PREFIX + memberId;
    return redisStringTemplate.opsForValue().get(key);
  }

  public void deleteMemberRefreshToken(Long memberId) {
    String key = REFRESH_TOKEN_PREFIX + memberId;
    redisStringTemplate.delete(key);
  }
}
