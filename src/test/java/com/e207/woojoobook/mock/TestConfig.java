package com.e207.woojoobook.mock;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;

@TestConfiguration
public class TestConfig {
	@Bean
	JavaMailSender javaMailSender() {
		return new MockJavaMailSender();
	}
}
