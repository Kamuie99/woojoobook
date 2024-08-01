package com.e207.woojoobook.api.extension.event;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.e207.woojoobook.api.verification.MailSender;
import com.e207.woojoobook.domain.rental.Rental;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ExtensionEventListener {

	@Value("${spring.mail.username}")
	private String from;
	private final MailSender mailSender;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleExtensionEvent(ExtensionEvent event) {
		sendMail(event);
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleExtensionResultEvent(ExtensionResultEvent event) {
		sendMail(event);
	}

	@Async
	protected void sendMail(ExtensionResultEvent event) {
		Rental rental = event.rental();
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setFrom(from);
		mailMessage.setTo(rental.getUser().getEmail());
		mailMessage.setSubject("우주도서 대여 연장 신청 결과");
		mailMessage.setText("대여번호 " + event.rental().getId() + "에 대한 연장 신청결과는 " + event.status().getDescription());
		this.mailSender.send(mailMessage);
	}

	@Async
	protected void sendMail(ExtensionEvent event) {
		Rental rental = event.rental();
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setFrom(from);
		mailMessage.setTo(rental.getUser().getEmail());
		mailMessage.setSubject("우주도서 대여 연장 신청 알림");
		mailMessage.setText("대여번호 " + event.rental().getId() + "에 대한 연장 신청이 도착했습니다.");
		this.mailSender.send(mailMessage);
	}
}
