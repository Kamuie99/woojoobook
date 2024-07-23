package com.e207.woojoobook.api.book.response;

import java.time.LocalDate;

import com.e207.woojoobook.domain.book.Book;

import lombok.Builder;

@Builder
public record BookResponse(String isbn, String title, String author, String publisher, LocalDate publicationDate,
						   String thumbnail, String description) {

	public static BookResponse of(Book book) {
		return BookResponse.builder()
			.isbn(book.getIsbn())
			.title(book.getTitle())
			.author(book.getAuthor())
			.publisher(book.getPublisher())
			.publicationDate(book.getPublicationDate())
			.thumbnail(book.getThumbnail())
			.description(book.getDescription())
			.build();
	}

	public Book toEntity() {
		return Book.builder()
			.isbn(this.isbn)
			.title(this.title)
			.author(this.author)
			.publisher(this.publisher)
			.publicationDate(this.publicationDate)
			.thumbnail(this.thumbnail)
			.description(this.description)
			.build();
	}
}
