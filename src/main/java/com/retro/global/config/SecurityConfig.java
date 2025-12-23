package com.retro.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // REST API는 CSRF 불필요
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-resources/**"
                ).permitAll()  // Swagger 관련 경로 허용
                .requestMatchers("/api/v1/auth/**").permitAll()  // 로그인 API는 인증 없이
                .requestMatchers("/api/test/**").permitAll()  // 테스트
                .anyRequest().authenticated()  // 위 엔드포인트 제외 auth 인증 필요

            // ).addFilterBefore()  // JWT 필터 추가 추후

            );

        return http.build();
    }
}

