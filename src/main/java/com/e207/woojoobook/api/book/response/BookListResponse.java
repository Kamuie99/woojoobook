package com.e207.woojoobook.api.book.response;

import java.util.List;

import lombok.Builder;

@Builder
public record BookListResponse(List<BookResponse> bookList) {
}