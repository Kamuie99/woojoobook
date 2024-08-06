package com.e207.woojoobook.api.userbook;

import static com.e207.woojoobook.domain.userbook.QualityStatus.*;
import static com.e207.woojoobook.domain.userbook.RegisterType.*;
import static java.lang.Boolean.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import com.e207.woojoobook.api.book.response.BookResponse;
import com.e207.woojoobook.api.user.response.UserResponse;
import com.e207.woojoobook.api.userbook.request.UserbookCreateRequest;
import com.e207.woojoobook.api.userbook.request.UserbookPageFindRequest;
import com.e207.woojoobook.api.userbook.request.UserbookUpdateRequest;
import com.e207.woojoobook.api.userbook.response.UserbookResponse;
import com.e207.woojoobook.domain.user.UserRepository;
import com.e207.woojoobook.domain.userbook.TradeStatus;
import com.e207.woojoobook.global.security.SecurityConfig;
import com.e207.woojoobook.restdocs.AbstractRestDocsTest;

@Import({SecurityConfig.class})
@WebMvcTest(controllers = UserbookController.class)
class UserbookControllerTest extends AbstractRestDocsTest {

	@MockBean
	private UserbookService userbookService;
	@MockBean
	private UserRepository userRepository;

	@WithMockUser
	@DisplayName("사용자 도서를 등록한다.")
	@Test
	void createSuccess() throws Exception {
		// given
		UserbookCreateRequest request = new UserbookCreateRequest("001", RENTAL_EXCHANGE, GOOD);
		String requestJson = objectMapper.writeValueAsString(request);

		UserbookResponse response = createUserbookResponse(1);
		String responseJson = objectMapper.writeValueAsString(response);
		given(userbookService.createUserbook(request)).willReturn(response);

		// expected
		mockMvc.perform(post("/userbooks")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson)
			)
			.andExpect(content().json(responseJson))
			.andExpect(status().isOk());
	}

	@WithMockUser
	@DisplayName("사용자 도서를 수정한다.")
	@Test
	void updateSuccess() throws Exception {
		// given
		Long userId = 1L;
		UserbookUpdateRequest request = new UserbookUpdateRequest(TRUE, TRUE, VERY_GOOD);
		String requestJson = objectMapper.writeValueAsString(request);

		UserbookResponse response = createUserbookResponse(1);
		String responseJson = objectMapper.writeValueAsString(response);
		given(userbookService.updateUserbook(userId, request)).willReturn(response);

		// expected
		mockMvc.perform(put("/userbooks/{userbookId}", userId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson)
			)
			.andExpect(content().json(responseJson))
			.andExpect(status().isOk());

	}

	@WithMockUser
	@DisplayName("사용자 도서 목록을 조회한다.")
	@Test
	void findListSuccess() throws Exception {
		// given
		UserbookPageFindRequest request = createFindRequest();
		String requestJson = objectMapper.writeValueAsString(request);

		Page<UserbookWithLikeResponse> response = createUserbookWithLikePage();
		String responseJson = objectMapper.writeValueAsString(response);
		given(userbookService.findUserbookPage(eq(request), any(Pageable.class))).willReturn(response);
		System.out.println("responseJson = " + responseJson);

		// expected
		mockMvc.perform(get("/userbooks")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson)
			)
			// .andExpect(content().json(responseJson)) // TODO <jhl221123> 테스트 수정 필요 
			.andExpect(status().isOk());
	}

	private UserbookPageFindRequest createFindRequest() {
		return new UserbookPageFindRequest("search keyword", List.of("2644056000", "2644053500", "2644054500"),
			RENTAL_EXCHANGE);
	}

	private Page<UserbookWithLikeResponse> createUserbookWithLikePage() {
		List<UserbookWithLikeResponse> userbookList = new ArrayList<>();
		for (int i = 1; i <= 3; i++) {
			UserbookResponse response = createUserbookResponse(i);
			userbookList.add(new UserbookWithLikeResponse(response, false));
		}
		return new PageImpl<>(userbookList, PageRequest.of(0, 3), 3);
	}

	private UserbookResponse createUserbookResponse(int idx) {
		return UserbookResponse.builder()
			.id((long)idx)
			.bookInfo(createBookResponse("00" + idx, "title" + idx))
			.ownerInfo(createUserResponse((long)idx, "user" + idx + "@email.com", "nickname" + idx))
			.registerType(RENTAL_EXCHANGE)
			.tradeStatus(TradeStatus.RENTAL_EXCHANGE_AVAILABLE)
			.qualityStatus(GOOD)
			.areaCode("2644056000")
			.build();
	}

	private UserResponse createUserResponse(Long id, String email, String nickname) {
		return UserResponse.builder()
			.id(id)
			.email(email)
			.nickname(nickname)
			.build();
	}

	private BookResponse createBookResponse(String isbn, String title) {
		return BookResponse.builder()
			.isbn(isbn)
			.title(title)
			.author("author")
			.publisher("publisher")
			.publicationDate(LocalDate.of(2021, 6, 15))
			.thumbnail("http://example.com/image.jpg")
			.description("description")
			.build();
	}

}