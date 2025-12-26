package com.retro.global.common.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;

public class HttpHeaderUtils {
  private static final String TOKEN_PREFIX = "Bearer ";

  public static String getTokenFromAuthHeader(HttpServletRequest request) {
    String headerValue = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (validateHeader(headerValue)) {
      return headerValue.substring(TOKEN_PREFIX.length());
    }
    return null;
  }

  private static boolean validateHeader(String headerValue) {
    return headerValue != null && headerValue.startsWith(TOKEN_PREFIX);
  }
}
