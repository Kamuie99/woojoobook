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

import com.e207.woojoobook.api.userbook.request.WishbookRequest;
import com.e207.woojoobook.api.userbook.response.WishbookResponse;
import com.e207.woojoobook.domain.user.UserRepository;
import com.e207.woojoobook.domain.userbook.WishbookRepository;
import com.e207.woojoobook.global.security.SecurityConfig;
import com.e207.woojoobook.restdocs.AbstractRestDocsTest;

@Import({SecurityConfig.class})
@WebMvcTest(controllers = WishbookController.class)
class WishbookControllerTest extends AbstractRestDocsTest {

	@MockBean
	private WishbookService wishBookService;
	@MockBean
	private WishbookRepository wishBookRepository;
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
	void updateWishbookRequest() throws Exception {
		// given
		boolean wished = false;
		WishbookRequest wishBookRequest = new WishbookRequest(userId, wished);
		given(wishBookService.updateWishbook(userbookId, wished))
			.willReturn(new WishbookResponse(userbookId, wished));

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
	void deleteWishbookRequest() throws Exception {
		WishbookRequest wishBookRequest = new WishbookRequest(1L, true);
		ResultActions resultActions = this.mockMvc.perform(
			post("/userbooks/{userbookId}/wish", userbookId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(wishBookRequest)));

		resultActions.andExpect(status().isOk());
	}
}