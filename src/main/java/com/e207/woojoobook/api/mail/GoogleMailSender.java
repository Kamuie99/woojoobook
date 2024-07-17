package com.e207.woojoobook.api.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class GoogleMailSender implements MailSender {

	private final JavaMailSender mailSender;
	@Value("${spring.mail.username}")
	private String from;

	@Async
	@Override
	public void send(VerificationMail verificationMail) {
		try {
			SimpleMailMessage mailMessage = new SimpleMailMessage();
			mailMessage.setFrom(from);
			mailMessage.setTo(verificationMail.getEmail());
			mailMessage.setSubject("우주도서 가입 인증번호 메일");
			mailMessage.setText(verificationMail.getVerificationCode());
			this.mailSender.send(mailMessage);
		} catch (MailException e) {
			// TODO : 예외처리
			throw new RuntimeException("잠시 후 다시 시도해주세요");
		}

	}
}

