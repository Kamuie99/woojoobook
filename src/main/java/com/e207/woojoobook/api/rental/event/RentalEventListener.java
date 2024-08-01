package com.e207.woojoobook.api.rental.event;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.e207.woojoobook.api.verification.MailSender;
import com.e207.woojoobook.domain.rental.Rental;
import com.e207.woojoobook.domain.userbook.TradeStatus;
import com.e207.woojoobook.domain.userbook.event.UserBookTradeStatusUpdateEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class RentalEventListener {

	@Value("${spring.mail.username}")
	private String from;
	private final MailSender mailSender;
	private final ApplicationEventPublisher eventPublisher;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleRentalRespond(RentalOfferEvent event) {
		Rental rental = sendMail(event);

		if (event.isApproved()) {
			eventPublisher.publishEvent(new UserBookTradeStatusUpdateEvent(rental.getUserbook(), TradeStatus.RENTED));
		}
	}

	@Async
	protected Rental sendMail(RentalOfferEvent event) {
		Rental rental = event.rental();
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setFrom(from);
		mailMessage.setTo(rental.getUser().getEmail());
		mailMessage.setSubject("우주도서 대여 신청번호 " + rental.getId() + "의 결과입니다.");
		mailMessage.setText(determineText(event.isApproved()));
		this.mailSender.send(mailMessage);
		return rental;
	}

	private String determineText(boolean isApproved) {
		if (isApproved) {
			return "도서 대여 신청이 승인되었습니다.";
		}
		return "도서 대여 신청이 거절되었습니다.";
	}

}
