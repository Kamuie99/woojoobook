package com.e207.woojoobook.client;

import java.util.Optional;

import com.e207.woojoobook.api.book.response.BookListResponse;
import com.e207.woojoobook.api.book.response.BookResponse;

public interface BookSearchClient {
	BookListResponse findBookByKeyword(String keyword, Integer page, Integer size);
	Optional<BookResponse> findBookByIsbn(String isbn);
}