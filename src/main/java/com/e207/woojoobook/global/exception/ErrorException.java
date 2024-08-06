package com.e207.woojoobook.global.exception;

import lombok.Getter;

@Getter
public class ErrorException extends RuntimeException {
	private final ErrorCode errorCode;

	public ErrorException(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}

	public ErrorException(ErrorCode errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}
}
