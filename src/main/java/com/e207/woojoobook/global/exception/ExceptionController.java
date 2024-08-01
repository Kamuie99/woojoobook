package com.e207.woojoobook.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class ExceptionController {

	@ExceptionHandler(ErrorException.class)
	public ResponseEntity<?> handleErrorException(ErrorException ex) {
		if(ex.getErrorCode().equals(ErrorCode.InternalServer)){
			log.warn(ex.getMessage());
		}
		return ResponseEntity.status(ex.getErrorCode().getStatus()).body(ex.getErrorCode().getMessage());
	}

}
