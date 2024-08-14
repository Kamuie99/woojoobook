package com.e207.woojoobook.api.book.response.aladin;

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
public class AladinBookResponse {

	@JsonProperty("totalResults")
	private Integer total;

	@JsonProperty("item")
	@Builder.Default
	private List<Item> items = new ArrayList<>();

	@Builder
	public record Item(@JsonProperty("isbn13") String isbn, String title, String author, String publisher,
					   String pubDate, @JsonProperty("cover") String thumbnail, String description) {

		public BookItem toBookItem() {
			return BookItem.builder()
				.isbn(this.isbn)
				.title(this.title)
				.author(this.author)
				.publisher(this.publisher)
				.publicationDate(LocalDate.parse(this.pubDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")))
				.thumbnail(this.thumbnail)
				.description(this.description)
				.build();
		}
	}
}