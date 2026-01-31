package com.retro.domain.member.application.exception;

import com.retro.global.common.exception.BusinessException;
import com.retro.global.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class MemberNotRegisteredException extends BusinessException {


  public MemberNotRegisteredException(ErrorCode errorCode) {
    super(errorCode);
  }

  public MemberNotRegisteredException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }
}
