package com.e207.woojoobook.api.book.response.naver;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.e207.woojoobook.api.book.response.BookItem;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class NaverBookResponse {

	private Integer total;

	@Builder.Default
	private List<Item> items = new ArrayList<>();

	@Builder
	public record Item(String isbn, String title, String author, String publisher, String description,
					   @JsonProperty("pubdate") String pubDate, @JsonProperty("image") String thumbnail) {

		public BookItem toBookItem() {
			return BookItem.builder()
				.isbn(this.isbn)
				.title(this.title)
				.author(this.author)
				.publisher(this.publisher)
				.publicationDate(LocalDate.parse(this.pubDate(), DateTimeFormatter.ofPattern("yyyyMMdd")))
				.thumbnail(this.thumbnail)
				.description(this.description)
				.build();
		}
	}
}