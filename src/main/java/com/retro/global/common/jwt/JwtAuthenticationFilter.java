package com.retro.global.common.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.retro.global.common.dto.ErrorResponse;
import com.retro.global.common.enums.PublicEndpoint;
import com.retro.global.common.exception.ErrorCode;
import com.retro.global.common.utils.HttpHeaderUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT 인증 필터
 * 모든 요청에서 JWT 토큰을 검증하고 SecurityContext에 인증 정보를 설정
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        
        // 1. Request Header에서 JWT 토큰 추출
        String token = HttpHeaderUtils.getTokenFromAuthHeader(request);
        
        if (token != null) {
            // 2. 토큰 상태 확인
            JwtProvider.TokenStatus tokenStatus = jwtProvider.checkToken(token);
            
            if (tokenStatus == JwtProvider.TokenStatus.VALID) {
                // 유효한 토큰 - 인증 정보 설정
                Authentication authentication = jwtProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
            } else if (tokenStatus == JwtProvider.TokenStatus.EXPIRED) {
                // 만료된 토큰 - 401 응답
                log.debug("만료된 토큰: {}", token);
                sendErrorResponse(response, ErrorCode.INVALID_TOKEN, "만료된 JWT 토큰입니다.");
                return;  // 필터 체인 중단
                
            } else {
                // 유효하지 않은 토큰 - 401 응답
                log.debug("유효하지 않은 토큰: {}", token);
                sendErrorResponse(response, ErrorCode.INVALID_TOKEN, "유효하지 않은 JWT 토큰입니다.");
                return;  // 필터 체인 중단
            }
        }
        
        // 3. 다음 필터로 진행
        filterChain.doFilter(request, response);
    }

    /**
     * 공개 엔드포인트는 필터를 건너뜀
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return PublicEndpoint.isPublic(uri);
    }

    /**
     * 에러 응답 전송
     */
    private void sendErrorResponse(
        HttpServletResponse response, 
        ErrorCode errorCode, 
        String message
    ) throws IOException {
        ErrorResponse errorResponse = ErrorResponse.of(errorCode, message);
        
        response.setStatus(errorResponse.getStatus());
        response.setContentType("application/json;charset=UTF-8");
        
        String jsonResponse = objectMapper
            .registerModule(new JavaTimeModule())
            .writeValueAsString(errorResponse);
        
        response.getWriter().write(jsonResponse);
    }
}
