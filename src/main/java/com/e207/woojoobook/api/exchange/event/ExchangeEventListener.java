package com.e207.woojoobook.api.exchange.event;

import static com.e207.woojoobook.domain.exchange.ExchangeStatus.*;
import static com.e207.woojoobook.domain.userbook.TradeStatus.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.e207.woojoobook.api.exchange.ExchangeService;
import com.e207.woojoobook.api.verification.MailSender;
import com.e207.woojoobook.domain.exchange.Exchange;
import com.e207.woojoobook.domain.exchange.ExchangeStatus;
import com.e207.woojoobook.domain.userbook.event.UserBookTradeStatusUpdateEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ExchangeEventListener {

	@Value("${spring.mail.username}")
	private String from;
	private final MailSender mailSender;
	private final ApplicationEventPublisher eventPublisher;
	private final ExchangeService exchangeService;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleExchangeRespond(ExchangeRespondEvent event) {
		Exchange exchange = exchangeService.findDomain(event.exchange().getId());
		sendMail(exchange);
		if (APPROVED.equals(exchange.getExchangeStatus())) {
			eventPublisher.publishEvent(new UserBookTradeStatusUpdateEvent(exchange.getSenderBook(), EXCHANGED));
			eventPublisher.publishEvent(new UserBookTradeStatusUpdateEvent(exchange.getReceiverBook(), EXCHANGED));
		}
	}

	@Async
	protected void sendMail(Exchange exchange) {
		SimpleMailMessage mailMessage = createMailMessage(exchange);
		this.mailSender.send(mailMessage);
	}

	private SimpleMailMessage createMailMessage(Exchange exchange) {
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setFrom(from);
		mailMessage.setTo(exchange.getSender().getEmail()); // TODO <jhl221123> 서비스 계층에서 페치조인 하지만 N+1 발생 가능
		mailMessage.setTo(exchange.getReceiver().getEmail());
		mailMessage.setSubject("우주도서 교환 신청번호 " + exchange.getId() + "의 결과입니다.");
		mailMessage.setText(determineText(exchange.getExchangeStatus()));
		return mailMessage;
	}

	private String determineText(ExchangeStatus status) {
		if (APPROVED.equals(status)) {
			return "도서 교환 신청이 승인되었습니다.";
		}
		return "도서 대여 신청이 거절되었습니다.";
	}
}
