package com.e207.woojoobook.api.book;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.e207.woojoobook.api.book.request.BookFindRequest;
import com.e207.woojoobook.api.book.response.BookListResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class BookController {
	private final BookService bookService;

	@GetMapping("/books")
	public ResponseEntity<BookListResponse> findBookList(@RequestBody BookFindRequest request) {
		return ResponseEntity.status(HttpStatus.OK).body(bookService.findBookList(request));
	}
}