package com.e207.woojoobook.api.book;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.e207.woojoobook.api.book.request.BookFindRequest;
import com.e207.woojoobook.api.book.response.BookItems;
import com.e207.woojoobook.client.BookSearchClient;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BookService {
	private final BookSearchClient naverBookSearchClient;

	@Transactional(readOnly = true)
	public BookItems findBookList(BookFindRequest request) {
		String keyword = request.keyword().trim();
		int page = request.page();
		int size = 20;
		int startPage = startPage(size, page);
		return naverBookSearchClient.findBookByKeyword(keyword, startPage, size);
	}

	private Integer startPage(Integer size, Integer page) {
		return size * (page - 1) + 1;
	}
}