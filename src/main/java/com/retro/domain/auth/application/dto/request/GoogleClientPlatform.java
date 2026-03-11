package com.retro.domain.auth.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "구글 OAuth 클라이언트 플랫폼")
@Getter
public enum GoogleClientPlatform {
  IOS,
  WEB,
  ANDROID
}