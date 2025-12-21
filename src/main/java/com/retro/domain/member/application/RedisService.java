package com.retro.domain.member.application;

import com.retro.domain.member.application.dto.response.OAuth2AppleMemberInfo;
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

  private final RedisTemplate<String, String> redisStringTemplate;
  @Value("${spring.jwt.register-valid-time}")
  private long validTimeForRegistration;

  public void saveTempMemberInfo(String uniqueTempId, OAuth2AppleMemberInfo memberInfo) {
    setTempMemberInfo(uniqueTempId, memberInfo, validTimeForRegistration);
  }

  public void setTempMemberInfo(String key,OAuth2AppleMemberInfo memberInfo , long ttlMillis) {
    redisStringTemplate.opsForHash().putAll(key, memberInfo.toMap());
    redisStringTemplate.expire(key, Duration.ofMillis(ttlMillis));
  }

  public OAuth2AppleMemberInfo getTempMemberInfo(String key) {
    Map<Object, Object> map = redisStringTemplate.opsForHash().entries(key);
    return OAuth2AppleMemberInfo.fromMap(map);
  }

  public void removeTempMemberInfo(String key) {
    redisStringTemplate.delete(key);
  }

}
