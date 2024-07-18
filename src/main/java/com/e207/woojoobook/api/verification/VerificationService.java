package com.e207.woojoobook.api.verification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.e207.woojoobook.api.verification.request.VerificationMail;
import com.e207.woojoobook.domain.user.UserVerification;
import com.e207.woojoobook.domain.user.UserVerificationRepository;
import com.e207.woojoobook.domain.user.VerificationCodeUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class VerificationService {

	private final JavaMailSender mailSender;
	private final UserVerificationRepository userVerificationRepository;
	@Value("${spring.mail.username}")
	private String from;

	@Async
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

	// TODO : 예외처리
	@Transactional(readOnly = true)
	public UserVerification findByEmail(String email) {
		return this.userVerificationRepository.findById(email)
			.orElseThrow(() -> new RuntimeException("유효하지 않는 email입니다."));
	}

	@Transactional
	public boolean verifyEmail(VerificationMail verificationMail) {
		// TODO : 예외처리
		UserVerification userVerification = this.userVerificationRepository.findById(verificationMail.getEmail())
			.orElseThrow(() -> new RuntimeException("존재하지 않는 email"));
		userVerification.verify(verificationMail.getVerificationCode());

		UserVerification save = this.userVerificationRepository.save(userVerification);

		return save.isVerified();
	}

	@Transactional
	public String createVerificationUser(String email) {
		UserVerification userVerification = UserVerification.builder()
			.email(email)
			.verificationCode(VerificationCodeUtil.generate())
			.isVerified(false)
			.build();

		return this.userVerificationRepository.save(userVerification).getVerificationCode();
	}
}

