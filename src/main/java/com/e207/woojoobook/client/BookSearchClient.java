package com.e207.woojoobook.client;

import com.e207.woojoobook.api.book.response.BookListResponse;

public interface BookSearchClient {
	BookListResponse findBookByKeyword(String keyword, Integer page, Integer size);
}

