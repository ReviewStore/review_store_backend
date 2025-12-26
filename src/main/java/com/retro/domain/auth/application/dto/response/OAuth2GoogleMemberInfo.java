package com.retro.domain.auth.application.dto.response;

import com.retro.domain.member.domain.entity.Provider;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

@Getter
@Builder
public class OAuth2GoogleMemberInfo {
    private String sub;           // Google 고유 ID (providerId)
    private String email;         // 이메일
    private String name;          // 이름
    private String picture;       // 프로필 이미지 URL
    private Provider provider;    // GOOGLE

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("sub", this.sub);
        map.put("email", this.email);
        map.put("name", this.name);
        map.put("picture", this.picture);
        map.put("provider", this.provider.name());
        return map;
    }

    public static OAuth2GoogleMemberInfo fromMap(Map<Object, Object> map) {
        if (CollectionUtils.isEmpty(map)) {
            return null;
        }

        String sub = (String) map.get("sub");
        String email = (String) map.get("email");
        String name = (String) map.get("name");
        String picture = (String) map.get("picture");
        String providerName = (String) map.get("provider");
        Provider provider = Provider.valueOf(providerName);

        return OAuth2GoogleMemberInfo.builder()
            .sub(sub)
            .email(email)
            .name(name)
            .picture(picture)
            .provider(provider)
            .build();
    }
}