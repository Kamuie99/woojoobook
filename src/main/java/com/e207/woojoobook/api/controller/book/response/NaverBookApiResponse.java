package com.e207.woojoobook.api.controller.book.response;

import java.util.List;

import lombok.Builder;

@Builder
public record NaverBookApiResponse(List<NaverBookItem> items) {
}