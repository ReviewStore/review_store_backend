package com.retro.global.common.dto;

import com.retro.global.common.enums.DeviceType;
import lombok.Getter;

@Getter
public class MemberDevice {

  private final DeviceType deviceType;

  public MemberDevice(DeviceType deviceType) {
    this.deviceType = deviceType;
  }
}
