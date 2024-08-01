package com.e207.woojoobook.global.exception;

import org.springframework.http.HttpStatus;

public record ErrorResponse(HttpStatus status, String message) {
}
