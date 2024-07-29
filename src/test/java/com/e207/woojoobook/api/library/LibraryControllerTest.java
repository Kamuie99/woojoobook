package com.e207.woojoobook.api.library;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.e207.woojoobook.api.library.request.LibraryCreateRequest;
import com.e207.woojoobook.api.library.request.LibraryUpdateRequest;
import com.e207.woojoobook.api.library.response.LibraryListResponse;
import com.e207.woojoobook.api.library.response.LibraryResponse;
import com.e207.woojoobook.domain.library.Library;
import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.global.security.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = LibraryController.class,
	excludeAutoConfiguration = SecurityAutoConfiguration.class,
	excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class))
class LibraryControllerTest {

	@MockBean
	private LibraryService libraryService;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	@DisplayName("나의 서재에서 카테고리 전체 목록을 조회한다")
	@Test
	void findList() throws Exception {
		// given
		// TODO: rest doc 적용 시 수정 필요
		User testUser = mock(User.class);
		when(testUser.getId()).thenReturn(1L);

		Library library1 = Library.builder()
			.user(testUser)
			.name("카테고리1")
			.bookList("책 리스트1")
			.orderNumber(2L)
			.build();
		Library library2 = Library.builder()
			.user(testUser)
			.name("카테고리2")
			.bookList("책 리스트2")
			.orderNumber(4L)
			.build();

		LibraryResponse libraryResponse1 = LibraryResponse.of(library1);
		LibraryResponse libraryResponse2 = LibraryResponse.of(library2);

		LibraryListResponse libraryListResponse = LibraryListResponse.builder()
			.libraryList(List.of(libraryResponse1, libraryResponse2))
			.build();
		given(this.libraryService.findList(testUser.getId())).willReturn(libraryListResponse);

		// when
		ResultActions resultActions = this.mockMvc.perform(get("/users/{userId}/libraries", testUser.getId()));

		// then
		resultActions.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(libraryListResponse)));
	}

	@DisplayName("나의 서재에 있는 카테고리 중 하나를 조회한다")
	@Test
	void findById() throws Exception {
		// given
		User testUser = mock(User.class);

		Library library = Library.builder()
			.user(testUser)
			.name("카테고리")
			.bookList("책 리스트")
			.orderNumber(2L)
			.build();
		LibraryResponse libraryResponse = LibraryResponse.of(library);
		String jsonResponse = objectMapper.writeValueAsString(libraryResponse);

		given(this.libraryService.find(eq(testUser.getId()), eq(1L))).willReturn(libraryResponse);

		// when
		ResultActions resultActions = this.mockMvc.perform(
			get("/users/{userId}/libraries/{categoryId}", testUser.getId(), 1L));

		// then
		resultActions.andExpect(status().isOk())
			.andExpect(content().json(jsonResponse));
	}

	@DisplayName("나의 서재에 새로운 카테고리를 생성한다")
	@Test
	void create() throws Exception {
		// given
		User testUser = mock(User.class);
		when(testUser.getId()).thenReturn(1L);

		Library library = Library.builder()
			.user(testUser)
			.name("카테고리")
			.bookList("책 리스트")
			.orderNumber(2L)
			.build();
		LibraryResponse libraryResponse = LibraryResponse.of(library);

		LibraryCreateRequest libraryCreateRequest = LibraryCreateRequest.builder()
			.categoryName("카테고리")
			.books("책 리스트")
			.build();
		given(this.libraryService.create(eq(testUser.getId()), eq(libraryCreateRequest))).willReturn(libraryResponse);

		// when
		ResultActions resultActions = this.mockMvc.perform(
			post("/users/{userId}/libraries/categories", testUser.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.objectMapper.writeValueAsString(libraryCreateRequest)));

		// then
		resultActions.andExpect(status().isCreated())
			.andExpect(content().json(objectMapper.writeValueAsString(libraryResponse)));
	}

	@DisplayName("나의 서재에 있는 카테고리 이름을 수정한다")
	@Test
	void updateName() throws Exception {
		// given
		User testUser = mock(User.class);
		when(testUser.getId()).thenReturn(1L);

		Library library = Library.builder()
			.user(testUser)
			.name("카테고리")
			.bookList("책 리스트")
			.orderNumber(2L)
			.build();
		LibraryResponse libraryResponse = LibraryResponse.of(library);

		LibraryUpdateRequest libraryUpdateRequest = LibraryUpdateRequest.builder()
			.categoryName("카테고리 수정")
			.books("책 리스트")
			.build();
		given(this.libraryService.update(eq(testUser.getId()), eq(1L), eq(libraryUpdateRequest))).willReturn(
			libraryResponse);

		// when
		ResultActions resultActions = this.mockMvc.perform(
			put("/users/{userId}/libraries/categories/{categoryId}", testUser.getId(), 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.objectMapper.writeValueAsString(libraryUpdateRequest)));

		// then
		resultActions.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(libraryResponse)));
	}

	@DisplayName("나의 서재에 있는 카테고리에 포함된 도서를 수정한다")
	@Test
	void updateBooks() throws Exception {
		// given
		User testUser = mock(User.class);
		when(testUser.getId()).thenReturn(1L);

		Library library = Library.builder()
			.user(testUser)
			.name("카테고리")
			.bookList("책 리스트")
			.orderNumber(2L)
			.build();
		LibraryResponse libraryResponse = LibraryResponse.of(library);

		LibraryUpdateRequest libraryUpdateRequest = LibraryUpdateRequest.builder()
			.categoryName("카테고리1")
			.books("변경된 책 리스트")
			.build();
		given(this.libraryService.update(eq(testUser.getId()), eq(1L), eq(libraryUpdateRequest))).willReturn(
			libraryResponse);

		// when
		ResultActions resultActions = this.mockMvc.perform(
			put("/users/{userId}/libraries/categories/{categoryId}", testUser.getId(), 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.objectMapper.writeValueAsString(libraryUpdateRequest)));

		// then
		resultActions.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(libraryResponse)));
	}

	@DisplayName("나의 서재에 있는 카테고리 순서를 변경한다")
	@Test
	void swapOrderNumber() throws Exception {
		// given
		User testUser = mock(User.class);
		when(testUser.getId()).thenReturn(1L);

		doNothing().when(libraryService).swapOrderNumber(eq(1L), eq(2L), eq(3L));

		// when
		ResultActions resultActions = this.mockMvc.perform(
			put("/users/{userId}/libraries/categories/{from}/{to}", testUser.getId(), 2L, 3L));

		// then
		resultActions.andExpect(status().isOk())
			.andDo(print());
		verify(libraryService).swapOrderNumber(1L, 2L, 3L);
	}

	@DisplayName("나의 서재에 있는 카테고리 하나를 삭제한다")
	@Test
	void delete() throws Exception {
		// given
		User testUser = mock(User.class);
		when(testUser.getId()).thenReturn(1L);

		Library library = Library.builder()
			.user(testUser)
			.name("카테고리1")
			.bookList("책 리스트")
			.orderNumber(2L)
			.build();

		doNothing().when(libraryService).delete(1L, library.getId());

		// when
		ResultActions resultActions = this.mockMvc.perform(
			MockMvcRequestBuilders.delete("/users/{userId}/libraries/categories/{id}", testUser.getId(), 1L));

		// then
		resultActions.andExpect(status().isNoContent())
			.andDo(print());
		verify(libraryService).delete(eq(1L), eq(1L));
	}
}
