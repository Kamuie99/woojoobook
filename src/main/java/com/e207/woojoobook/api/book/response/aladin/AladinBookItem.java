package com.e207.woojoobook.api.book.response.aladin;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.e207.woojoobook.api.book.response.BookResponse;

import lombok.Builder;

@Builder
public record AladinBookItem(String isbn13, String title, String author, String publisher, String pubDate, String cover,
							 String description) {

	public BookResponse toBookResponse() {
		return new BookResponse(this.isbn13(), this.title(), this.author(), this.publisher(),
			LocalDate.parse(this.pubDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")), this.cover(),
			this.description());
	}
}