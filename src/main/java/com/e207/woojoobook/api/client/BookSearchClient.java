package com.e207.woojoobook.api.client;

import com.e207.woojoobook.api.controller.book.response.BookListResponse;

public interface BookSearchClient {
	BookListResponse findBookByKeyword(String keyword, Integer page, Integer size);
}

