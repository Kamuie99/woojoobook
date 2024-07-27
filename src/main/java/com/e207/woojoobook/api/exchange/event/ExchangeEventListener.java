package com.e207.woojoobook.api.exchange.event;

import static com.e207.woojoobook.domain.userbook.TradeStatus.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.e207.woojoobook.domain.exchange.Exchange;
import com.e207.woojoobook.domain.exchange.ExchangeRepository;
import com.e207.woojoobook.domain.userbook.event.UserBookTradeStatusUpdateEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ExchangeEventListener {

	@Value("${spring.mail.username}")
	private String from;
	private final JavaMailSender mailSender;
	private final ApplicationEventPublisher eventPublisher;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleExchangeRespond(ExchangeRespondEvent event) {
		Exchange exchange = sendMail(event);
		if (event.isApproved()) {
			eventPublisher.publishEvent(new UserBookTradeStatusUpdateEvent(exchange.getSenderBook(), EXCHANGED));
			eventPublisher.publishEvent(new UserBookTradeStatusUpdateEvent(exchange.getReceiverBook(), EXCHANGED));
		}
	}

	@Async
	protected Exchange sendMail(ExchangeRespondEvent event) {
		Exchange exchange = event.exchange();
		SimpleMailMessage mailMessage = createMailMessage(event, exchange);
		this.mailSender.send(mailMessage);
		return exchange;
	}

	private SimpleMailMessage createMailMessage(ExchangeRespondEvent event, Exchange exchange) {
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setFrom(from);
		mailMessage.setTo(exchange.getSenderBook().getUser().getEmail()); // TODO <jhl221123> N+1, 회원 엔티티 참조 고려
		mailMessage.setTo(exchange.getReceiverBook().getUser().getEmail()); // TODO <jhl221123> N+1, 회원 엔티티 참조 고려
		mailMessage.setSubject("우주도서 교환 신청번호 " + exchange.getId() + "의 결과입니다.");
		mailMessage.setText(determineText(event.isApproved()));
		return mailMessage;
	}

	private String determineText(boolean isApproved) {
		if (isApproved) {
			return "도서 교환 신청이 승인되었습니다.";
		}
		return "도서 대여 신청이 거절되었습니다.";
	}
}
