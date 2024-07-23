package com.e207.woojoobook.api.book;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.e207.woojoobook.api.book.request.BookFindRequest;
import com.e207.woojoobook.api.book.response.BookListResponse;
import com.e207.woojoobook.api.book.response.BookResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(BookController.class)
class BookControllerTest {

	@MockBean
	private BookService bookService;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	@WithMockUser
	@DisplayName("도서 제목을 검색한다")
	@Test
	void find_bookList() throws Exception {
		// given
		BookFindRequest bookFindRequest = new BookFindRequest("galaxy", 1);

		BookResponse bookResponse = BookResponse.builder()
			.isbn("1234567890")
			.title("Galaxies and Stars")
			.author("Neil deGrasse Tyson")
			.publisher("Cosmos Publishing")
			.publicationDate(LocalDate.of(2021, 6, 15))
			.thumbnail("http://example.com/image.jpg")
			.description("A comprehensive guide to the universe.")
			.build();

		BookListResponse mockResponse = new BookListResponse(List.of(bookResponse));

		given(this.bookService.findBookList(any(BookFindRequest.class))).willReturn(mockResponse);

		// when
		ResultActions resultActions = this.mockMvc.perform(get("/books")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(bookFindRequest)));

		// then
		resultActions.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(mockResponse)));
	}

	@WithMockUser
	@DisplayName("일치하는 결과가 없는 도서 제목을 검색한다")
	@Test
	void find_bookList_noResult() throws Exception {
		// given
		BookFindRequest bookFindRequest = new BookFindRequest("NoExistenBook", 1);

		BookListResponse mockResponse = new BookListResponse(Collections.emptyList());
		given(this.bookService.findBookList(any(BookFindRequest.class))).willReturn(mockResponse);

		// when
		ResultActions resultActions = this.mockMvc.perform(get("/books")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(bookFindRequest)));

		// then
		resultActions.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(mockResponse)));
	}

	@WithMockUser
	@DisplayName("공백을 검색한다")
	@Test
	void find_bookList_blank() throws Exception {
		// given
		BookFindRequest bookFindRequest = new BookFindRequest(" ", 1);

		BookListResponse mockResponse = new BookListResponse(Collections.emptyList());
		given(this.bookService.findBookList(any(BookFindRequest.class))).willReturn(mockResponse);

		// when
		ResultActions resultActions = this.mockMvc.perform(get("/books")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(bookFindRequest)));

		// then
		resultActions.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(mockResponse)));
	}
}