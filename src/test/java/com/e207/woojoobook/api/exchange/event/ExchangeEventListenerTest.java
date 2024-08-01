package com.e207.woojoobook.api.exchange.event;

import static com.e207.woojoobook.domain.exchange.ExchangeStatus.*;
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

import com.e207.woojoobook.api.exchange.ExchangeService;
import com.e207.woojoobook.api.verification.MailSender;
import com.e207.woojoobook.domain.exchange.Exchange;
import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.userbook.event.UserBookTradeStatusUpdateEvent;

@ExtendWith(MockitoExtension.class)
class ExchangeEventListenerTest {

	@InjectMocks
	ExchangeEventListener exchangeEventListener;

	@Mock
	MailSender javaMailSender;

	@Mock
	ApplicationEventPublisher applicationEventPublisher;

	@Mock
	ExchangeService exchangeService;

	@DisplayName("교환 신청을 수락하면 책 상태를 변경하는 이벤트가 발행된다.")
	@Test
	void handleExchangeRespondWithApprove() {
		// given
		User sender = createUser(1L);
		User receiver = createUser(2L);
		Exchange exchange = createExchange(1L, sender, receiver);
		exchange.respond(APPROVED);
		ExchangeRespondEvent event = createEvent(exchange);

		given(exchangeService.findDomain(exchange.getId())).willReturn(exchange);

		// when
		exchangeEventListener.handleExchangeRespond(event);

		//then
		verify(applicationEventPublisher, times(2)).publishEvent(any(UserBookTradeStatusUpdateEvent.class));
	}

	@DisplayName("교환 신청을 거절하면 책 상태를 변경하는 이벤트가 발행되지 않는다.")
	@Test
	void handleExchangeRespondWithReject() {
		// given
		User sender = createUser(1L);
		User receiver = createUser(2L);
		Exchange exchange = createExchange(1L, sender, receiver);
		exchange.respond(REJECTED);
		ExchangeRespondEvent event = createEvent(exchange);

		given(exchangeService.findDomain(exchange.getId())).willReturn(exchange);

		// when
		exchangeEventListener.handleExchangeRespond(event);

		//then
		verify(applicationEventPublisher, times(0)).publishEvent(any(UserBookTradeStatusUpdateEvent.class));
	}

	@DisplayName("교환 이벤트 발생 시, 메일이 발송된다.")
	@Test
	void sendMailSuccess() {
		// given
		User sender = createUser(1L);
		User receiver = createUser(2L);
		Exchange exchange = createExchange(1L, sender, receiver);
		ExchangeRespondEvent event = createEvent(exchange);

		given(exchangeService.findDomain(exchange.getId())).willReturn(exchange);

		// when
		exchangeEventListener.handleExchangeRespond(event);

		//then
		verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
	}

	private ExchangeRespondEvent createEvent(Exchange exchange) {
		return ExchangeRespondEvent.builder()
			.exchange(exchange)
			.build();
	}

	private Exchange createExchange(Long id, User sender, User receiver) {
		return Exchange.builder()
			.id(id)
			.sender(sender)
			.receiver(receiver)
			.build();
	}

	private static User createUser(Long id) {
		return User.builder()
			.id(id)
			.build();
	}
}