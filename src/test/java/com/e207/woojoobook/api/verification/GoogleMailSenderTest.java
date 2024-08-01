package com.e207.woojoobook.api.verification;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.AopTestUtils;

@SpringBootTest
class GoogleMailSenderTest {

	@Autowired
	private MailSender mailSender;

	@Test
	void DI_test() {
		GoogleMailSender actualMailSender = AopTestUtils.getTargetObject(mailSender);
		assertInstanceOf(GoogleMailSender.class, actualMailSender);
	}

}