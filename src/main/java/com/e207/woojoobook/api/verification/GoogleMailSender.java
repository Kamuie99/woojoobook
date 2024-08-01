package com.e207.woojoobook.api.verification;

import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Profile("!local")
@Component
public class GoogleMailSender implements MailSender{

	private final JavaMailSender mailSender;

	@Async
	@Override
	public void send(SimpleMailMessage message) {
		this.mailSender.send(message);
	}
}
