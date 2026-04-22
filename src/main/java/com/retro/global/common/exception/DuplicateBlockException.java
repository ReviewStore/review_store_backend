package com.retro.global.common.exception;

public class DuplicateBlockException extends BusinessException {
  public DuplicateBlockException() {
	super(ErrorCode.DUPLICATE_BLOCK);
  }
}


