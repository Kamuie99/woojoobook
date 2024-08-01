package com.e207.woojoobook.api.verification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.e207.woojoobook.api.user.request.VerificationMail;
import com.e207.woojoobook.domain.user.UserVerification;
import com.e207.woojoobook.domain.user.VerificationCodeUtil;
import com.e207.woojoobook.global.exception.ErrorCode;
import com.e207.woojoobook.global.exception.ErrorException;
import com.e207.woojoobook.global.mail.Mail;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class VerificationService {

	private final MailSender mailSender;
	private final RedisTemplate<String, UserVerification> redisTemplate;
	@Value("${spring.mail.username}")
	private String from;

	@Async
	public void send(VerificationMail verificationMail) {
		try {
			SimpleMailMessage message = Mail.of(from, verificationMail.getEmail(), "우주도서 가입 인증번호 메일",
				verificationMail.getVerificationCode());

			this.mailSender.send(message);
		} catch (MailException e) {
			throw new ErrorException(ErrorCode.InternalServer);
		}
	}

	@Transactional(readOnly = true)
	public UserVerification findByEmail(String email) {
		UserVerification userVerification = (UserVerification) this.redisTemplate.opsForHash().get("user;", email);
		if(userVerification == null) {
			throw new ErrorException(ErrorCode.NotFound);
		}
		return userVerification;
	}

	@Transactional
	public boolean verifyEmail(VerificationMail verificationMail) {
		UserVerification userVerification = (UserVerification) this.redisTemplate.opsForHash().get("user;", verificationMail.getEmail());
		if(userVerification == null) {
			throw new ErrorException(ErrorCode.NotFound);
		}
		userVerification.verify(verificationMail.getVerificationCode());
		this.redisTemplate.opsForHash().put("user;", verificationMail.getEmail(), userVerification);
		UserVerification saved = (UserVerification) this.redisTemplate.opsForHash().get("user;", verificationMail.getEmail());
		return saved.isVerified();
	}

	@Transactional
	public String createVerificationUser(String email) {
		UserVerification userVerification = UserVerification.builder()
			.email(email)
			.verificationCode(VerificationCodeUtil.generate())
			.isVerified(false)
			.build();

		this.redisTemplate.opsForHash().put("user;", email, userVerification);
		UserVerification verification = (UserVerification) this.redisTemplate.opsForHash().get("user;", email);
		return verification.getVerificationCode();
	}
}

