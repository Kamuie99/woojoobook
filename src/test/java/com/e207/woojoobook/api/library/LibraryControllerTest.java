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

		Library library1 = new Library(testUser, "카테고리1", "책 리스트1", 2L);
		Library library2 = new Library(testUser, "카테고리2", "책 리스트2", 4L);

		LibraryResponse libraryResponse1 = LibraryResponse.of(library1);
		LibraryResponse libraryResponse2 = LibraryResponse.of(library2);

		LibraryListResponse libraryListResponse = new LibraryListResponse(List.of(libraryResponse1, libraryResponse2));
		given(this.libraryService.findList()).willReturn(libraryListResponse);

		// when
		ResultActions resultActions = this.mockMvc.perform(get("/libraries"));

		// then
		resultActions.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(libraryListResponse)));
	}

	@DisplayName("나의 서재에 있는 카테고리 중 하나를 조회한다")
	@Test
	void findById() throws Exception {
		// given
		User testUser = mock(User.class);

		Library library = new Library(testUser, "카테고리1", "책 리스트", 2L);
		LibraryResponse libraryResponse = LibraryResponse.of(library);
		String jsonResponse = objectMapper.writeValueAsString(libraryResponse);

		given(this.libraryService.find(eq(1L))).willReturn(libraryResponse);

		// when
		ResultActions resultActions = this.mockMvc.perform(get("/libraries/{id}", 1L));

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

		Library library = new Library(testUser, "카테고리1", "책 리스트", 2L);
		LibraryResponse libraryResponse = LibraryResponse.of(library);

		LibraryCreateRequest libraryCreateRequest = new LibraryCreateRequest("카테고리1", "책 리스트");
		given(this.libraryService.create(eq(libraryCreateRequest))).willReturn(libraryResponse);

		// when
		ResultActions resultActions = this.mockMvc.perform(post("/libraries/categories")
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

		Library library = new Library(testUser, "카테고리1", "책 리스트", 2L);
		LibraryResponse libraryResponse = LibraryResponse.of(library);

		LibraryUpdateRequest libraryUpdateRequest = new LibraryUpdateRequest("카테고리 수정", "책 리스트");
		given(this.libraryService.update(eq(1L), eq(libraryUpdateRequest))).willReturn(libraryResponse);

		// when
		ResultActions resultActions = this.mockMvc.perform(put("/libraries/categories/{id}", 1L)
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

		Library library = new Library(testUser, "카테고리1", "책 리스트", 2L);
		LibraryResponse libraryResponse = LibraryResponse.of(library);

		LibraryUpdateRequest libraryUpdateRequest = new LibraryUpdateRequest("카테고리1", "변경된 책 리스트");
		given(this.libraryService.update(eq(1L), eq(libraryUpdateRequest))).willReturn(libraryResponse);

		// when
		ResultActions resultActions = this.mockMvc.perform(put("/libraries/categories/{id}", 1L)
			.contentType(MediaType.APPLICATION_JSON)
			.content(this.objectMapper.writeValueAsString(libraryUpdateRequest)));

		// then
		resultActions.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(libraryResponse)));
	}

	@DisplayName("나의 서재에 있는 카테고리 순서를 변경한다")
	@Test
	void swapOrderNumberNumber() throws Exception {
		// given
		doNothing().when(libraryService).swapOrderNumber(eq(2L), eq(3L));

		// when
		ResultActions resultActions = this.mockMvc.perform(put("/libraries/categories/{from}/{to}", 2L, 3L));

		// then
		resultActions.andExpect(status().isOk())
			.andDo(print());
		verify(libraryService).swapOrderNumber(2L, 3L);
	}

	@DisplayName("나의 서재에 있는 카테고리 하나를 삭제한다")
	@Test
	void delete() throws Exception {
		// given
		doNothing().when(libraryService).delete(1L);

		// when
		ResultActions resultActions = this.mockMvc.perform(
			MockMvcRequestBuilders.delete("/libraries/categories/{id}", 1L));

		// then
		resultActions.andExpect(status().isNoContent())
			.andDo(print());
		verify(libraryService).delete(eq(1L));
	}
}
