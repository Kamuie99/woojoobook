package com.e207.woojoobook.api.book.request;

import java.util.Objects;

import lombok.Builder;

@Builder
public record BookFindRequest(String keyword, Integer page) {
	public BookFindRequest {
		Objects.requireNonNull(keyword);
		Objects.requireNonNull(page);
	}
}