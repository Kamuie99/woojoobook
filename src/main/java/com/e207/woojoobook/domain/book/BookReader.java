package com.e207.woojoobook.domain.book;

import org.springframework.stereotype.Component;

import com.e207.woojoobook.api.book.response.BookItem;
import com.e207.woojoobook.client.BookSearchClient;
import com.e207.woojoobook.global.exception.ErrorCode;
import com.e207.woojoobook.global.exception.ErrorException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class BookReader {

	private BookRepository bookRepository;
	private BookSearchClient naverBookSearchClient;

	public Book findBookOrSave(String isbn) {
		return bookRepository.findById(isbn).orElseGet(() -> processBookNotExist(isbn));
	}

	private Book processBookNotExist(String isbn) {
		return naverBookSearchClient.findBookByIsbn(isbn)
			.map(BookItem::toEntity)
			.map(bookRepository::save)
			.orElseThrow(() -> new ErrorException(ErrorCode.NotFound));
	}
}
