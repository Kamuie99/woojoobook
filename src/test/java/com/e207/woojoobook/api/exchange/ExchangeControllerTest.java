package com.e207.woojoobook.api.exchange;

import static com.e207.woojoobook.domain.exchange.ExchangeStatus.*;
import static com.e207.woojoobook.domain.exchange.ExchangeUserCondition.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;

import com.e207.woojoobook.api.exchange.request.ExchangeCreateRequest;
import com.e207.woojoobook.api.exchange.request.ExchangeFindCondition;
import com.e207.woojoobook.api.exchange.request.ExchangeOfferFindCondition;
import com.e207.woojoobook.api.exchange.request.ExchangeOfferRespondRequest;
import com.e207.woojoobook.global.security.SecurityConfig;
import com.e207.woojoobook.restdocs.AbstractRestDocsTest;

@WebMvcTest(
	controllers = ExchangeController.class,
	excludeAutoConfiguration = SecurityAutoConfiguration.class,
	excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
)
class ExchangeControllerTest extends AbstractRestDocsTest {

	@MockBean
	ExchangeService exchangeService;

	@DisplayName("교환 신청한다.")
	@Test
	void createSuccess() throws Exception {
		// given
		ExchangeCreateRequest request = ExchangeCreateRequest.builder()
			.senderBookId(1L)
			.receiverBookId(2L)
			.build();
		String requestJson = objectMapper.writeValueAsString(request);

		// expected
		mockMvc.perform(post("/userbooks/{receiverBookId}/exchanges/offer/{senderBookId}", 2L, 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson)
			)
			.andExpect(status().isCreated());
	}

	@DisplayName("교환 내역을 조회한다.")
	@Test
	void findExchangesSuccess() throws Exception {
		// given
		ExchangeFindCondition request = new ExchangeFindCondition(APPROVED);
		String requestJson = objectMapper.writeValueAsString(request);

		// expected
		mockMvc.perform(get("/exchanges")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson)
			)
			.andExpect(status().isOk());
	}

	@DisplayName("교환 신청 내역을 조회한다.")
	@Test
	void findExchangeOffer() throws Exception {
		// given
		ExchangeOfferFindCondition request = new ExchangeOfferFindCondition(SENDER_RECEIVER);
		String requestJson = objectMapper.writeValueAsString(request);

		// expected
		mockMvc.perform(get("/exchanges/offer")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson)
			)
			.andExpect(status().isOk());
	}

	@DisplayName("교환 신청에 응답한다.")
	@Test
	void offerRespondSuccess() throws Exception {
		// given
		ExchangeOfferRespondRequest request = new ExchangeOfferRespondRequest(APPROVED);
		String requestJson = objectMapper.writeValueAsString(request);

		// expected
		mockMvc.perform(post("/exchanges/offer/{id}", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson)
			)
			.andExpect(status().isOk())
			.andDo(document("exchange-controller-test/offer-respond-success",
				requestFields(
					fieldWithPath("status").description("APPROVED or REJECTED")
				) // TODO <jhl221123> 내역 조회 메서드 수정 후, 하나의 테스트로 통합 필요
			));
	}

	@DisplayName("교환 신청을 취소한다.")
	@Test
	void deleteSuccess() throws Exception {
		// expected
		mockMvc.perform(delete("/exchanges/offer/{id}", 1L)
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isNoContent());
	}
}