package com.e207.woojoobook.api.exchange.event;

import static java.lang.Boolean.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.e207.woojoobook.api.user.event.UserBookTradeStatusEvent;
import com.e207.woojoobook.domain.exchange.Exchange;
import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.userbook.Userbook;

@ExtendWith(MockitoExtension.class)
class ExchangeEventListenerTest {

	@InjectMocks
	ExchangeEventListener exchangeEventListener;

	@Mock
	JavaMailSender javaMailSender;

	@Mock
	ApplicationEventPublisher applicationEventPublisher;

	@DisplayName("교환 신청을 수락하면 책 상태를 변경하는 이벤트가 발행된다.")
	@Test
	void handleExchangeRespondWithApprove() {
		// given
		ExchangeRespondEvent event = createEvent(TRUE);

		// when
		exchangeEventListener.handleExchangeRespond(event);

		//then
		verify(applicationEventPublisher, times(2)).publishEvent(any(UserBookTradeStatusEvent.class));
	}

	@DisplayName("교환 신청을 거절하면 책 상태를 변경하는 이벤트가 발행되지 않는다.")
	@Test
	void handleExchangeRespondWithReject() {
		// given
		ExchangeRespondEvent event = createEvent(FALSE);

		// when
		exchangeEventListener.handleExchangeRespond(event);

		//then
		verify(applicationEventPublisher, times(0)).publishEvent(any(UserBookTradeStatusEvent.class));
	}

	@DisplayName("교환 이벤트 발생 시, 메일이 발송된다.")
	@Test
	void sendMailSuccess() {
		// given
		ExchangeRespondEvent event = createEvent(TRUE);

		// when
		exchangeEventListener.sendMail(event);

		//then
		verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
	}

	private ExchangeRespondEvent createEvent(Boolean isApproved) {
		return ExchangeRespondEvent.builder()
			.exchange(createExchange())
			.isApproved(isApproved)
			.build();
	}

	private Exchange createExchange() {
		return Exchange.builder()
			.senderBook(createUserbook())
			.receiverBook(createUserbook())
			.build();
	}

	private User createUser() {
		return User.builder()
			.email("user@email.com")
			.build();
	}

	private Userbook createUserbook() {
		return Userbook.builder()
			.user(createUser())
			.build();
	}
}