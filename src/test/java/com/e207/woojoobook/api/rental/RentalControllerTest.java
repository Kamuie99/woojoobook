package com.e207.woojoobook.api.rental;

import static com.e207.woojoobook.domain.rental.RentalStatus.*;
import static com.e207.woojoobook.domain.rental.RentalUserCondition.*;
import static com.e207.woojoobook.domain.userbook.QualityStatus.*;
import static com.e207.woojoobook.domain.userbook.RegisterType.*;
import static com.e207.woojoobook.domain.userbook.TradeStatus.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import com.e207.woojoobook.api.rental.request.RentalFindCondition;
import com.e207.woojoobook.api.rental.request.RentalOfferRespondRequest;
import com.e207.woojoobook.api.rental.response.RentalOfferResponse;
import com.e207.woojoobook.api.rental.response.RentalResponse;
import com.e207.woojoobook.domain.book.Book;
import com.e207.woojoobook.domain.rental.Rental;
import com.e207.woojoobook.domain.rental.RentalStatus;
import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.user.UserRepository;
import com.e207.woojoobook.domain.userbook.Userbook;
import com.e207.woojoobook.global.security.SecurityConfig;
import com.e207.woojoobook.restdocs.AbstractRestDocsTest;

@Import({SecurityConfig.class})
@WebMvcTest(controllers = RentalController.class)
class RentalControllerTest extends AbstractRestDocsTest {

	@MockBean
	private RentalService rentalService;
	@MockBean
	private UserRepository userRepository;

	@WithMockUser
	@DisplayName("회원이 도서에 대한 대여를 신청한다")
	@Test
	void createRentalRequest() throws Exception {
		// given
		Long targetBookId = 1L;
		Long validRentalId = 2L;
		given(rentalService.rentalOffer(targetBookId)).willReturn(new RentalOfferResponse(validRentalId));

		// when
		ResultActions resultActions = this.mockMvc.perform(
			post("/userbooks/{targetBookId}/rentals/offer", targetBookId));

		// then
		resultActions.andExpect(status().isCreated())
			.andExpect(jsonPath("$.rentalId").exists());
	}

	@WithMockUser
	@DisplayName("조건에 해당하는 대여 내역을 조회한다.")
	@Test
	void findRentalByCondition() throws Exception {
		// given
		RentalFindCondition request = new RentalFindCondition(SENDER_RECEIVER, OFFERING);
		String requestJson = objectMapper.writeValueAsString(request);

		PageImpl<RentalResponse> response = createRentalPage();
		String responseJson = objectMapper.writeValueAsString(response);

		doReturn(response).when(rentalService).findByCondition(any(), any());

		// expected
		mockMvc.perform(get("/rentals")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson)
			)
			.andExpect(status().isOk())
			.andExpect(content().json(responseJson))
			.andDo((document("rental-controller-test/find-rental-by-condition",
				preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("userCondition").description("SENDER / RECEIVER / SENDER_RECEIVER"),
					fieldWithPath("rentalStatus").description("OFFERING / REJECTED / APPROVED / IN_PROGRESS")
				))
			));
	}

	@WithMockUser
	@DisplayName("회원이 도서 대여 신청에 대해 응답한다")
	@Test
	void respondRentalOffer() throws Exception {
		// given
		Long offerId = 1L;
		boolean isApproved = true;

		// when
		ResultActions resultActions = this.mockMvc.perform(put("/rentals/offer/{offerId}", offerId)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(new RentalOfferRespondRequest(isApproved))));

		// then
		resultActions.andExpect(status().isOk());
	}

	@WithMockUser
	@DisplayName("회원이 발생한 대여신청을 삭제한다")
	@Test
	void deleteRentalOffer() throws Exception {
		// given
		Long offerId = 1L;

		// when
		ResultActions resultActions =
			this.mockMvc.perform(delete("/rentals/offer/{offerId}", offerId));

		// then
		resultActions.andExpect(status().isOk());
	}

	@WithMockUser
	@DisplayName("도서 소유자가 반납완료를 한다")
	@Test
	void returnSuccess() throws Exception {
		// given
		Long rentalId = 1L;

		// when
		ResultActions resultActions =
			this.mockMvc.perform(put("/rentals/{rentalId}/return", rentalId));

		// then
		resultActions.andExpect(status().isOk());
	}

	private User createUser(Long id, String nickname) {
		return User.builder()
			.id(id)
			.email("user@email.com")
			.password("encrypted password")
			.nickname(nickname)
			.areaCode("1234567")
			.build();
	}

	private Book createBook(String isbn) {
		return Book.builder()
			.isbn(isbn)
			.title("title")
			.author("author")
			.publisher("publisher")
			.publicationDate(LocalDate.of(2024, 7, 22))
			.thumbnail("thumbnail")
			.description("description")
			.build();
	}

	private Userbook createUserbook(Long id, User user, String isbn) {
		return Userbook.builder()
			.id(id)
			.book(createBook(isbn))
			.user(user)
			.qualityStatus(NORMAL)
			.registerType(RENTAL)
			.tradeStatus(RENTAL_AVAILABLE)
			.build();
	}

	private RentalResponse createRental(Long id, User user, Userbook userbook, RentalStatus rentalStatus) {
		Rental rental = Rental.builder()
			.id(id)
			.user(user)
			.userbook(userbook)
			.rentalStatus(rentalStatus)
			.build();
		return RentalResponse.of(rental);
	}

	private PageImpl<RentalResponse> createRentalPage() {
		User me = createUser(1L, "me");
		User user = createUser(2L, "someone");
		Userbook mine = createUserbook(1L, me, "001");
		Userbook userbook = createUserbook(2L, user, "002");
		RentalResponse rental1 = createRental(1L, me, userbook, OFFERING);
		RentalResponse rental2 = createRental(2L, user, mine, OFFERING);
		return new PageImpl<>(List.of(rental1, rental2));
	}
}