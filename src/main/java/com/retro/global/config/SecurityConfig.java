package com.retro.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.retro.global.common.jwt.JwtAuthenticationFilter;
import com.retro.global.common.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextHolderFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .httpBasic(HttpBasicConfigurer::disable)
            .csrf(CsrfConfigurer::disable)
            .formLogin(FormLoginConfigurer::disable)
            .sessionManagement(
                configure ->
                    configure.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .securityContext(
                securityContext ->
                    securityContext.securityContextRepository(
                        new HttpSessionSecurityContextRepository()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-resources/**"
                ).permitAll()  // Swagger 관련 경로 허용
                .requestMatchers("/api/v1/auth/**").permitAll()  // 로그인 API는 인증 없이
                .requestMatchers("/api/test/**").permitAll()  // 테스트
                .anyRequest().authenticated()  // 위 엔드포인트 제외 auth 인증 필요
             )// 괄호 오류 수정 및 필터 위치 조정
            .addFilterBefore(new JwtAuthenticationFilter(jwtProvider, objectMapper),
                SecurityContextHolderFilter.class);
        return http.build();
    }
}

