package com.e207.woojoobook.api.book;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.ResultActions;

import com.e207.woojoobook.api.book.request.BookFindRequest;
import com.e207.woojoobook.api.book.response.BookListResponse;
import com.e207.woojoobook.api.book.response.BookResponse;
import com.e207.woojoobook.global.security.SecurityConfig;
import com.e207.woojoobook.restdocs.AbstractRestDocsTest;

@WebMvcTest(controllers = BookController.class,
	excludeAutoConfiguration = SecurityAutoConfiguration.class,
	excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class))
class BookControllerTest extends AbstractRestDocsTest {

	@MockBean
	private BookService bookService;

	@DisplayName("도서 제목을 검색한다")
	@Test
	void find_bookList() throws Exception {
		// given
		BookFindRequest bookFindRequest = BookFindRequest.builder()
			.keyword("galaxy")
			.page(1)
			.build();

		BookResponse bookResponse = BookResponse.builder()
			.isbn("1234567890")
			.title("Galaxies and Stars")
			.author("Neil deGrasse Tyson")
			.publisher("Cosmos Publishing")
			.publicationDate(LocalDate.of(2021, 6, 15))
			.thumbnail("http://example.com/image.jpg")
			.description("A comprehensive guide to the universe.")
			.build();

		BookListResponse mockResponse = BookListResponse.builder()
			.maxPage(1)
			.bookList(List.of(bookResponse))
			.build();

		given(this.bookService.findBookList(eq(bookFindRequest))).willReturn(mockResponse);

		// when
		ResultActions resultActions = this.mockMvc.perform(get("/books")
			.param("keyword", bookFindRequest.keyword())
			.param("page", String.valueOf(bookFindRequest.page())));

		// then
		resultActions.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(mockResponse)));
	}

	@DisplayName("일치하는 결과가 없는 도서 제목을 검색한다")
	@Test
	void find_bookList_noResult() throws Exception {
		// given
		BookFindRequest bookFindRequest = BookFindRequest.builder()
			.keyword("NoExistBook")
			.page(1)
			.build();

		BookListResponse mockResponse = BookListResponse.builder()
			.maxPage(0)
			.bookList(Collections.emptyList())
			.build();

		given(this.bookService.findBookList(eq(bookFindRequest))).willReturn(mockResponse);

		// when
		ResultActions resultActions = this.mockMvc.perform(get("/books")
			.param("keyword", bookFindRequest.keyword())
			.param("page", String.valueOf(bookFindRequest.page())));

		// then
		resultActions.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(mockResponse)));
	}
}