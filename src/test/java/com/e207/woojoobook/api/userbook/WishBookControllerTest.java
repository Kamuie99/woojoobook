package com.e207.woojoobook.api.userbook;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import com.e207.woojoobook.api.userbook.request.WishBookRequest;
import com.e207.woojoobook.api.userbook.response.WishBookResponse;
import com.e207.woojoobook.domain.user.UserRepository;
import com.e207.woojoobook.domain.userbook.WishBookRepository;
import com.e207.woojoobook.global.security.SecurityConfig;
import com.e207.woojoobook.restdocs.AbstractRestDocsTest;

@Import({SecurityConfig.class})
@WebMvcTest(controllers = WishBookController.class)
class WishBookControllerTest extends AbstractRestDocsTest {

	@MockBean
	private WishBookService wishBookService;
	@MockBean
	private WishBookRepository wishBookRepository;
	@MockBean
	private UserRepository userRepository;

	private Long userId;
	private Long userbookId;

	@BeforeEach
	void setUp() {
		userId = 1L;
		userbookId = 1L;
	}

	@WithMockUser
	@DisplayName("회원이 사용자 도서에 관심 등록 요청을 보냄")
	@Test
	void updateWishBookRequest() throws Exception {
		// given
		boolean wished = false;
		WishBookRequest wishBookRequest = new WishBookRequest(userId, wished);
		given(wishBookService.updateWishBook(userbookId, wished))
			.willReturn(new WishBookResponse(userbookId, wished));

		// when
		ResultActions resultActions = this.mockMvc.perform(
			post("/userbooks/{userbookId}/wish", userbookId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(wishBookRequest)));

		// then
		resultActions.andExpect(status().isOk());
	}

	@WithMockUser
	@DisplayName("관심 등록된 사용자 도서에 관심 등록 취소 요청을 보냄")
	@Test
	void deleteWishBookRequest() throws Exception {
		WishBookRequest wishBookRequest = new WishBookRequest(1L, true);
		ResultActions resultActions = this.mockMvc.perform(
			post("/userbooks/{userbookId}/wish", userbookId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(wishBookRequest)));

		resultActions.andExpect(status().isOk());
	}
}