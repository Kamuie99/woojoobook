package com.e207.woojoobook.api.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.e207.woojoobook.api.mail.MailSender;
import com.e207.woojoobook.api.mail.VerificationMail;
import com.e207.woojoobook.domain.user.UserMasterRepository;
import com.e207.woojoobook.domain.user.UserSlaveRepository;
import com.e207.woojoobook.domain.user.UserVerification;
import com.e207.woojoobook.domain.user.UserVerificationRepository;
import com.e207.woojoobook.domain.user.VerificationCodeUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

	private final UserVerificationRepository userVerificationRepository;
	private final UserSlaveRepository userSlaveRepository;
	private final UserMasterRepository userMasterRepository;
	private final PasswordEncoder passwordEncoder;
	private final MailSender mailSender;

	@Transactional
	public void join(UserCreateRequest userCreateRequest) {
		UserVerification userVerification = this.userVerificationRepository.findById(userCreateRequest.getEmail())
			.orElseThrow(() -> new RuntimeException("유효하지 않는 email입니다."));
		if (!userVerification.isVerified()) {
			throw new RuntimeException("인증되지 않은 회원입니다.");
		}

		userCreateRequest.encode(this.passwordEncoder.encode(userCreateRequest.getPassword()));

		this.userMasterRepository.save(userCreateRequest.toEntity());
	}

	@Transactional
	public boolean verifyEmail(VerificationMail verificationMail) {
		UserVerification userVerification = this.userVerificationRepository.findById(verificationMail.getEmail())
			.orElseThrow(() -> new RuntimeException("존재하지 않는 email"));
		userVerification.verify(verificationMail.getVerificationCode());

		UserVerification save = this.userVerificationRepository.save(userVerification);

		return save.isVerified();
	}

	@Transactional
	public void sendMail(EmailCodeCreateRequest request) {
		String email = request.getEmail();
		String verificationCode = createVerificationUser(email);

		VerificationMail verificationMail = VerificationMail.builder()
			.email(email)
			.verificationCode(verificationCode)
			.build();

		this.mailSender.send(verificationMail);
	}

	private String createVerificationUser(String email) {
		UserVerification userVerification = UserVerification.builder()
			.email(email)
			.verificationCode(VerificationCodeUtil.generate())
			.isVerified(false)
			.build();

		return this.userVerificationRepository.save(userVerification).getVerificationCode();
	}
}
