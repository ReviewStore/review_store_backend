package com.retro.global.config;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import com.retro.global.common.enums.PublicEndpoint;
import com.retro.global.common.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        // CSRF 비활성화 (JWT 사용 시 불필요)
        .csrf(csrf -> csrf.disable())

        // 세션 사용 안 함 (JWT 방식이므로)
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )

        // 요청별 인증 설정
        .authorizeHttpRequests(auth -> auth
            // 공개 URL 허용 (PublicEndpoint에서 관리)
            .requestMatchers(PublicEndpoint.getAll().toArray(new String[0])).permitAll()
            .requestMatchers(POST, "/api/v1/notices").hasRole("ADMIN")
            .requestMatchers(GET, "/api/v1/notices/**")
            .hasAnyRole("MEMBER", "ADMIN")
            // 그 외 모든 요청은 인증 필요
            .anyRequest().authenticated()
        )

        // JWT 필터 추가
        .addFilterBefore(
            jwtAuthenticationFilter,
            UsernamePasswordAuthenticationFilter.class
        );

    return http.build();
  }
}
