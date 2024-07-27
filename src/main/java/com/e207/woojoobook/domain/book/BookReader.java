package com.e207.woojoobook.domain.book;

import org.springframework.stereotype.Component;

import com.e207.woojoobook.api.book.response.BookResponse;
import com.e207.woojoobook.client.BookSearchClient;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class BookReader {

	private BookRepository bookRepository;
	private BookSearchClient bookSearchClient;

	public Book findBookOrSave(String isbn) {
		return bookRepository.findById(isbn).orElseGet(() -> processBookNotExist(isbn));
	}

	private Book processBookNotExist(String isbn) {
		return bookSearchClient.findBookByIsbn(isbn)
			.map(BookResponse::toEntity)
			.map(bookRepository::save)
			.orElseThrow(() -> new RuntimeException("검색한 책이 존재하지 않을 때 던지는 예외"));    // todo 예외 처리
	}
}
