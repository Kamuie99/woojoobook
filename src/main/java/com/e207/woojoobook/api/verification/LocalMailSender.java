package com.e207.woojoobook.api.verification;

import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Profile("!prod")
@Component
public class LocalMailSender implements MailSender {
	@Override
	public void send(SimpleMailMessage message) {
	}
}
