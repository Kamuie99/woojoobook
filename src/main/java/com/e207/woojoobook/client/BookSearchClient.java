package com.e207.woojoobook.client;

import java.util.Optional;

import com.e207.woojoobook.api.book.response.BookItem;
import com.e207.woojoobook.api.book.response.BookItems;

public interface BookSearchClient {
	BookItems findBookByKeyword(String keyword, Integer page, Integer size);

	Optional<BookItem> findBookByIsbn(String isbn);

	default BookItems fallbackByKeyword(BookSearchClient bookSearchClient, String keyword, Integer page,
		Integer size) {
		return bookSearchClient.findBookByKeyword(keyword, page, size);
	}

	default Optional<BookItem> fallbackByIsbn(BookSearchClient bookSearchClient, String isbn) {
		return bookSearchClient.findBookByIsbn(isbn);
	}
}