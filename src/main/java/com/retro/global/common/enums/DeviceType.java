package com.retro.global.common.enums;

import java.util.Arrays;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum DeviceType {
  WEB("Web"),
  APP("App");

  private final String name;

  public static DeviceType getDeviceType(String userAgent) {
    return Arrays.stream(DeviceType.values())
        .filter(deviceType -> deviceType.name.equals(userAgent))
        .findFirst()
        .orElse(DeviceType.APP);
  }
}
