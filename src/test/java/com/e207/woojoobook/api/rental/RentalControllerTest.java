package com.e207.woojoobook.api.rental;

import static com.e207.woojoobook.domain.rental.RentalStatus.*;
import static com.e207.woojoobook.domain.rental.RentalUserCondition.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.e207.woojoobook.api.rental.request.RentalFindCondition;
import com.e207.woojoobook.api.rental.request.RentalOfferRespondRequest;
import com.e207.woojoobook.api.rental.response.RentalOfferResponse;
import com.e207.woojoobook.domain.user.UserRepository;
import com.e207.woojoobook.global.security.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

@Import({SecurityConfig.class})
@WebMvcTest(controllers = RentalController.class)
class RentalControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
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
	@DisplayName("대여 신청 내역을 조회한다.")
	@Test
	void findRentalOffer() throws Exception {
		// given
		RentalFindCondition request = new RentalFindCondition(SENDER_RECEIVER, OFFERING);
		String requestJson = objectMapper.writeValueAsString(request);

		// expected
		mockMvc.perform(get("/rentals/offer")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson)
			)
			.andDo(print())
			.andExpect(status().isOk());
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
	void checkReturnRental() throws Exception {
		// given
		Long rentalId = 1L;

		// when
		ResultActions resultActions =
			this.mockMvc.perform(put("/rentals/{rentalId}/return", rentalId));

		// then
		resultActions.andExpect(status().isOk());
	}
}