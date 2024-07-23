package com.e207.woojoobook.api.book.response;

import lombok.Builder;

@Builder
public record NaverBookItem(String title, String link, String image, String author, String price, String discount,
							String publisher, String pubdate, String isbn, String description) {
}