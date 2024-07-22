package com.e207.woojoobook.global.mail;

import org.springframework.mail.SimpleMailMessage;

import lombok.Builder;

@Builder
public record Mail(String from,String to,String title,String text) {

	public static SimpleMailMessage of(String from, String to, String title, String text) {
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setFrom(from);
		mailMessage.setTo(to);
		mailMessage.setSubject(title);
		mailMessage.setText(text);
		return mailMessage;
	}
}
