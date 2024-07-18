package com.e207.woojoobook.domain.book;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Entity
public class Book {
	@Id
	private String isbn;
	private String title;
	private String author;
	private String publisher;
	private LocalDate publicationDate;
	private String thumbnail;
	private String description;

	@Builder
	private Book(String isbn, String title, String author, String publisher, LocalDate publicationDate,
		String thumbnail, String description) {
		this.isbn = isbn;
		this.title = title;
		this.author = author;
		this.publisher = publisher;
		this.publicationDate = publicationDate;
		this.thumbnail = thumbnail;
		this.description = description;
	}
}