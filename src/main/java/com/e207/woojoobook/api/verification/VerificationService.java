package com.e207.woojoobook.api.verification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.e207.woojoobook.api.user.request.VerificationMail;
import com.e207.woojoobook.domain.user.UserVerification;
import com.e207.woojoobook.domain.user.UserVerificationRepository;
import com.e207.woojoobook.domain.user.VerificationCodeUtil;
import com.e207.woojoobook.global.exception.ErrorCode;
import com.e207.woojoobook.global.exception.ErrorException;
import com.e207.woojoobook.global.mail.Mail;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class VerificationService {

	private final MailSender mailSender;
	private final UserVerificationRepository userVerificationRepository;

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
		return this.userVerificationRepository.findByEmail(email)
			.orElseThrow(() -> new ErrorException(ErrorCode.NotFound));
	}

	@Transactional
	public boolean verifyEmail(VerificationMail verificationMail) {
		UserVerification userVerification = findByEmail(
			verificationMail.getEmail());
		userVerification.verify(verificationMail.getVerificationCode());
		UserVerification saved = this.userVerificationRepository.save(userVerification);
		return saved.isVerified();
	}

	@Transactional
	public String createVerificationUser(String email) {
		UserVerification userVerification = UserVerification.builder()
			.email(email)
			.verificationCode(VerificationCodeUtil.generate())
			.isVerified(false)
			.build();

		UserVerification saved = this.userVerificationRepository.save(userVerification);
		UserVerification verification = this.userVerificationRepository.findById(saved.getId())
			.orElseThrow(() -> new ErrorException(ErrorCode.NotFound));

		return verification.getVerificationCode();
	}
}

