package com.e207.woojoobook.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ErrorException extends RuntimeException{
	private final ErrorCode errorCode;
}
