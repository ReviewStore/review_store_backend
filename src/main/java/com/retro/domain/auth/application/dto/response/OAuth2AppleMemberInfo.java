package com.retro.domain.auth.application.dto.response;

import com.retro.domain.member.domain.entity.Provider;
import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.CollectionUtils;

@Builder
@Getter
public class OAuth2AppleMemberInfo {
  private String sub;
  private Provider provider;

  public Map<String, Object> toMap() {
    Map<String, Object> map = new HashMap<>();
    map.put("sub", this.sub);
    map.put("provider", this.provider.name());
    return map;
  }

  public static OAuth2AppleMemberInfo fromMap(Map<Object, Object> map) {
    if (CollectionUtils.isEmpty(map)) {
      return null;
    }

    String sub = (String) map.get("sub");
    String providerName = (String) map.get("provider");
    Provider provider = Provider.valueOf(providerName);

    return OAuth2AppleMemberInfo.builder()
        .sub(sub).provider(provider)
        .build();
  }
}
