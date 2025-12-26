package com.retro.global.common.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.retro.global.common.dto.ErrorResponse;
import com.retro.global.common.exception.BusinessException;
import com.retro.global.common.exception.ErrorCode;
import com.retro.global.common.utils.HttpHeaderUtils;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtProvider jwtProvider;
  private final ObjectMapper objectMapper;
  private final SecurityContextHolderStrategy securityContextHolderStrategy =
      SecurityContextHolder.getContextHolderStrategy();
  private final SecurityContextRepository securityContextRepository =
      new DelegatingSecurityContextRepository(
          new HttpSessionSecurityContextRepository(),
          new RequestAttributeSecurityContextRepository());

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      String token = HttpHeaderUtils.getTokenFromAuthHeader(request);

      if (isEmptyToken(token)) {
        filterChain.doFilter(request, response);
        return;
      }
      try {
        Authentication authentication = jwtProvider.getAuthentication(token);
        setAuthenticationToContext(authentication, request, response);
      } catch (BusinessException e) {
        // BusinessException 발생 시 (ErrorCode 기반 응답)
        sendErrorResponse(response, ErrorResponse.of(e.getErrorCode(), e.getMessage()));
        return;
      } catch (JwtException e) {
        // 기타 JWT 라이브러리 예외 발생 시
        sendErrorResponse(response, ErrorResponse.of(ErrorCode.INVALID_TOKEN, e.getMessage()));
        return;
      }
      filterChain.doFilter(request, response);
    } catch (Exception e) {
      log.error("필터 내부에서 예상치 못한 예외가 발생하였습니다.", e);
      sendErrorResponse(response, ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR));
    }
  }

  private boolean isEmptyToken(String token) {
    return !StringUtils.hasText(token);
  }

  private void setAuthenticationToContext(
      Authentication authentication,
      HttpServletRequest request,
      HttpServletResponse response) {
    SecurityContext securityContext =
        securityContextHolderStrategy.createEmptyContext(); // 매번 새 컨텍스트 생성
    securityContext.setAuthentication(authentication);
    securityContextHolderStrategy.setContext(securityContext);
    securityContextRepository.saveContext(securityContext, request, response);
  }

  private void sendErrorResponse(HttpServletResponse response, ErrorResponse errorResponse) throws IOException {
    setResponseOption(response, errorResponse);
    String result = createResponseValueForError(errorResponse);
    writeResponse(response, result);
  }

  private void writeResponse(HttpServletResponse response, String result) throws IOException {
    response.getWriter().write(result);
  }

  private String createResponseValueForError(ErrorResponse errorResponse) throws JsonProcessingException {
    return objectMapper.registerModule(new JavaTimeModule()) // LocalDateTime 처리를 위해 필요
        .writeValueAsString(errorResponse);
  }

  private void setResponseOption(HttpServletResponse response, ErrorResponse errorResponse) {
    response.setStatus(errorResponse.getStatus());
    response.setContentType("application/json;charset=UTF-8");
  }
}
