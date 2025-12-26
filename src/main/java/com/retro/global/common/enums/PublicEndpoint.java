package com.retro.global.common.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PublicEndpoint {
    
    // 인증 API
    AUTH_API("/api/v1/auth/**"),
    
    // 테스트 API (개발 환경)
//    TEST_API("/api/test/**"),
    
    // Swagger/API 문서
    SWAGGER_UI("/swagger-ui/**"),
    API_DOCS("/v3/api-docs/**"),
    SWAGGER_RESOURCES("/swagger-resources/**"),
    
    // 오류 페이지
    ERROR("/error"),
    
    // 정적 리소스
    FAVICON("/favicon.ico");

    private final String url;

    public static List<String> getAll() {
        return Arrays.stream(values())
            .map(PublicEndpoint::getUrl)
            .collect(Collectors.toList());
    }

    public static boolean isPublic(String uri) {
        return Arrays.stream(values())
            .anyMatch(endpoint -> matches(uri, endpoint.getUrl()));
    }

    private static boolean matches(String uri, String pattern) {
        if (pattern.endsWith("/**")) {
            // 와일드카드 패턴 처리
            String prefix = pattern.substring(0, pattern.length() - 3);
            return uri.startsWith(prefix);
        } else {
            // 정확한 경로 매칭
            return uri.equals(pattern);
        }
    }
}
