package com.retro.global.common.utils;

import com.retro.global.common.dto.LoginMemberInfo;
import com.retro.global.common.exception.BusinessException;
import com.retro.global.common.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

  public Long getAuthenticatedUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (validateAuthentication(authentication)) {
      LoginMemberInfo loginMemberInfo = getLoginMemberInfo(authentication);
      return loginMemberInfo.id();
    }
    throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
  }

  private LoginMemberInfo getLoginMemberInfo(Authentication authentication) {
    return (LoginMemberInfo) authentication.getPrincipal();
  }

  public boolean validateAuthentication(Authentication authentication) {
    return authentication != null
        && authentication.getPrincipal() instanceof LoginMemberInfo loginMemberInfo;
  }


}
