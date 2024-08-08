package com.e207.woojoobook.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
	// 공통
	NotFound(HttpStatus.NOT_FOUND, "존재하지 않습니다."),
	InvalidAccess(HttpStatus.BAD_REQUEST, "접근할 수 없는 상태입니다."),
	UserNotFound(HttpStatus.UNAUTHORIZED, "인증 정보가 없습니다."),
	ForbiddenError(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
	BadRequest(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
	IllegalArgument(HttpStatus.BAD_REQUEST, "인자 값이 잘못되었습니다."),
	InternalServer(HttpStatus.INTERNAL_SERVER_ERROR, "잠시 후 다시 시도해주세요"),

	// 회원
	NotEnoughPoint(HttpStatus.BAD_REQUEST, "포인트가 부족합니다."),
	InvalidPassword(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
	NotAcceptDuplicate(HttpStatus.BAD_REQUEST, "이미 존재합니다."),

	// 도서

	// 대여
	DuplicateRented(HttpStatus.INTERNAL_SERVER_ERROR, "동일한 도서가 여러 번 대여되었습니다.");

	// 교환

	private final HttpStatus status;
	private final String message;
}
