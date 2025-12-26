package com.retro.domain.auth.application;
import com.retro.domain.auth.application.dto.response.OAuth2GoogleMemberInfo;
import com.retro.domain.member.domain.entity.Provider;
import com.retro.global.common.exception.BusinessException;
import com.retro.global.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoogleOAuth2Service {

    private static final String GOOGLE_TOKEN_INFO_URL = "https://oauth2.googleapis.com/tokeninfo?id_token=";

    @Value("${spring.oauth2.google.client-id}")
    private String clientId;

    private final WebClient webClient;

    public OAuth2GoogleMemberInfo processGoogleLogin(String idToken) {
        try {
            // 1. Google tokeninfo API로 ID Token 검증
            Map<String, Object> tokenInfo = verifyIdTokenWithGoogle(idToken);

            // 2. Client ID 검증 (보안)
            String aud = (String) tokenInfo.get("aud");
            if (!clientId.equals(aud)) {
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

    private Map<String, Object> verifyIdTokenWithGoogle(String idToken) {
        Map<String, Object> tokenInfo = webClient.get()
            .uri(GOOGLE_TOKEN_INFO_URL + idToken)
            .retrieve()
            .bodyToMono(Map.class)
            .block();

        if (tokenInfo == null || tokenInfo.isEmpty()) throw new BusinessException(ErrorCode.INVALID_TOKEN);

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