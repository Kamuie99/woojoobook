package com.e207.woojoobook.api.verification;

import org.springframework.mail.SimpleMailMessage;

public interface MailSender {

	void send(SimpleMailMessage message);
}
