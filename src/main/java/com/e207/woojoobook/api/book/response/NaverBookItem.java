package com.e207.woojoobook.api.book.response;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import lombok.Builder;

@Builder
public record NaverBookItem(String title, String link, String image, String author, String price, String discount,
							String publisher, String pubdate, String isbn, String description) {

	public BookResponse toBookResponse() {
		return new BookResponse(this.isbn(), this.title(), this.author(), this.publisher(),
			LocalDate.parse(this.pubdate(), DateTimeFormatter.ofPattern("yyyyMMdd")), this.image(), this.description());
	}
}