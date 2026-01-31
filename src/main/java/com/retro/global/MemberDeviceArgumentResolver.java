package com.retro.global;

import com.retro.global.common.dto.MemberDevice;
import com.retro.global.common.enums.DeviceType;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class MemberDeviceArgumentResolver implements HandlerMethodArgumentResolver {

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.getParameterType().equals(MemberDevice.class);

  }

  @Override
  public @Nullable Object resolveArgument(MethodParameter parameter,
      @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
      @Nullable WebDataBinderFactory binderFactory) throws Exception {
    String userAgent = webRequest.getHeader(HttpHeaders.USER_AGENT);
    return new MemberDevice(DeviceType.getDeviceType(userAgent));
  }
}
