package com.e207.woojoobook.domain.exchange;

import static com.e207.woojoobook.domain.exchange.ExchangeStatus.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.userbook.Userbook;

class ExchangeTest {

	@Test
	@DisplayName("교환을 수락하면 날짜가 입력되고 교환이 완료된다.")
	void respondApprove() {
		// given
		Userbook senderUserBook = createUserBook();
		Userbook receiverUserBook = createUserBook();
		Exchange exchange = createExchange(senderUserBook, receiverUserBook);

		// when
		exchange.respond(APPROVED);

		//then
		assertThat(exchange.getExchangeDate()).isNotNull();
		assertThat(exchange.getExchangeStatus()).isEqualTo(APPROVED);
	}

	@Test
	@DisplayName("교환을 거절하면 날짜가 입력되지 않고, 상태만 변경된다.")
	void respondReject() {
		// given
		Userbook senderUserBook = createUserBook();
		Userbook receiverUserBook = createUserBook();
		Exchange exchange = createExchange(senderUserBook, receiverUserBook);

		// when
		exchange.respond(REJECTED);

		//then
		assertThat(exchange.getExchangeDate()).isNull();
		assertThat(exchange.getExchangeStatus()).isEqualTo(REJECTED);
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