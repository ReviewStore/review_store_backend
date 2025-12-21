package com.retro.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        // JWT 인증 설정
        String jwtSchemeName = "Bearer Authentication";
        SecurityRequirement securityRequirement = new SecurityRequirement()
            .addList(jwtSchemeName);

        Components components = new Components()
            .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                .name(jwtSchemeName)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT 토큰을 입력하세요 (Bearer 제외)"));

        return new OpenAPI()
            .info(new Info()
                .title("회고집 API")
                .description("면접 회고 서비스 API 문서")
                .version("v1.0.0"))
            .addServersItem(new Server()
                .url("http://localhost:8080")
                .description("로컬 서버"))
            .addSecurityItem(securityRequirement)
            .components(components);
    }
}
