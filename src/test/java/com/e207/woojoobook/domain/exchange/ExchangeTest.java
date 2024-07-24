package com.e207.woojoobook.domain.exchange;

import static com.e207.woojoobook.domain.exchange.ExchangeStatus.*;
import static java.lang.Boolean.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.userbook.Userbook;

class ExchangeTest {

	@Test
	@DisplayName("교환을 수락하면 날짜가 입력되고 교환이 완료된다.")
	void changeExchangeStatusApprove() {
		// given
		Userbook senderUserBook = createUserBook();
		Userbook receiverUserBook = createUserBook();
		Exchange exchange = createExchange(senderUserBook, receiverUserBook);

		// when
		exchange.respond(TRUE);

		//then
		assertThat(exchange.getExchangeDate()).isNotNull();
		assertThat(exchange.getExchangeStatus()).isEqualTo(COMPLETED);
	}

	@Test
	@DisplayName("교환을 거절하면 날짜가 입력되지 않고, 상태만 변경된다.")
	void changeExchangeStatusReject() {
		// given
		Userbook senderUserBook = createUserBook();
		Userbook receiverUserBook = createUserBook();
		Exchange exchange = createExchange(senderUserBook, receiverUserBook);

		// when
		exchange.respond(FALSE);

		//then
		assertThat(exchange.getExchangeDate()).isNull();
		assertThat(exchange.getExchangeStatus()).isEqualTo(REJECTED);
	}

	@Test
	@DisplayName("교환 날짜를 등록한다.")
	void registerExchangeDate() {
		// given
		Userbook senderUserBook = createUserBook();
		Userbook receiverUserBook = createUserBook();
		Exchange exchange = createExchange(senderUserBook, receiverUserBook);
		LocalDateTime exchangeDate = LocalDateTime.of(2024, 7, 22, 9, 10);

		// when
		exchange.registerExchangeDate(exchangeDate);

		//then
		assertThat(exchange.getExchangeDate()).isEqualTo(exchangeDate);
	}

	private Exchange createExchange(Userbook senderUserBook, Userbook receiverUserBook) {
		return Exchange.builder()
			.senderBook(senderUserBook)
			.receiverBook(receiverUserBook)
			.build();
	}

	private Userbook createUserBook() {
		return Userbook.builder()
			.user(User.builder().build())
			.build();
	}
}