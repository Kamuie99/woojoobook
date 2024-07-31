package com.e207.woojoobook.api.exchange;

import static com.e207.woojoobook.domain.exchange.ExchangeStatus.*;
import static com.e207.woojoobook.domain.exchange.ExchangeUserCondition.*;
import static com.e207.woojoobook.domain.userbook.QualityStatus.*;
import static com.e207.woojoobook.domain.userbook.RegisterType.*;
import static com.e207.woojoobook.domain.userbook.TradeStatus.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;

import com.e207.woojoobook.api.exchange.request.ExchangeCreateRequest;
import com.e207.woojoobook.api.exchange.request.ExchangeFindCondition;
import com.e207.woojoobook.api.exchange.request.ExchangeOfferRespondRequest;
import com.e207.woojoobook.api.exchange.response.ExchangeResponse;
import com.e207.woojoobook.domain.book.Book;
import com.e207.woojoobook.domain.exchange.Exchange;
import com.e207.woojoobook.domain.exchange.ExchangeStatus;
import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.userbook.Userbook;
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

	@DisplayName("조건에 해당하는 교환 내역을 조회한다.")
	@Test
	void findExchangesByConditionSuccess() throws Exception {
		// given
		ExchangeFindCondition request = new ExchangeFindCondition(SENDER_RECEIVER, APPROVED);
		String requestJson = objectMapper.writeValueAsString(request);

		PageImpl<ExchangeResponse> response = createExchangePage();
		String responseJson = objectMapper.writeValueAsString(response);

		doReturn(response).when(exchangeService).findByCondition(any(), any());

		// expected
		mockMvc.perform(get("/exchanges")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson)
			)
			.andExpect(status().isOk())
			.andExpect(content().json(responseJson))
			.andDo((document("exchange-controller-test/find-exchanges-by-condition-success",
				preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("userCondition").description("SENDER / RECEIVER / SENDER_RECEIVER"),
					fieldWithPath("exchangeStatus").description("REJECTED / APPROVED / IN_PROGRESS")
				))
			));
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

	private User createUser(Long id) {
		return User.builder()
			.id(id)
			.email("user@email.com")
			.password("encrypted password")
			.nickname("nickname")
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

	private Userbook createUserbook(Long id, String isbn, Long userId) {
		return Userbook.builder()
			.id(id)
			.book(createBook(isbn))
			.user(createUser(userId))
			.qualityStatus(NORMAL)
			.registerType(EXCHANGE)
			.tradeStatus(EXCHANGE_AVAILABLE)
			.build();
	}

	private ExchangeResponse createExchange(Userbook senderBook, Userbook receiverBook, ExchangeStatus status) {
		Exchange exchange = Exchange.builder()
			.sender(senderBook.getUser())
			.receiver(receiverBook.getUser())
			.senderBook(senderBook)
			.receiverBook(receiverBook)
			.build();
		exchange.respond(status);
		return ExchangeResponse.of(exchange);
	}

	private PageImpl<ExchangeResponse> createExchangePage() {
		Userbook senderBook = createUserbook(1L, "001", 1L);
		Userbook receiverBook = createUserbook(2L, "002", 2L);
		ExchangeResponse exchange1 = createExchange(senderBook, receiverBook, APPROVED);
		ExchangeResponse exchange2 = createExchange(receiverBook, senderBook, APPROVED);
		return new PageImpl<>(List.of(exchange1, exchange2));
	}
}