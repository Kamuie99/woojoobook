package com.e207.woojoobook.api.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.e207.woojoobook.api.user.request.EmailCodeCreateRequest;
import com.e207.woojoobook.api.user.request.UserCreateRequest;
import com.e207.woojoobook.api.verification.VerificationService;
import com.e207.woojoobook.api.verification.request.VerificationMail;
import com.e207.woojoobook.domain.user.UserMasterRepository;
import com.e207.woojoobook.domain.user.UserSlaveRepository;
import com.e207.woojoobook.domain.user.UserVerification;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {
	private final UserSlaveRepository userSlaveRepository;
	private final UserMasterRepository userMasterRepository;
	private final PasswordEncoder passwordEncoder;
	private final VerificationService verificationService;

	@Transactional
	public void join(UserCreateRequest userCreateRequest) {
		validateUserCreateRequest(userCreateRequest);

		userCreateRequest.encode(this.passwordEncoder.encode(userCreateRequest.getPassword()));

		this.userMasterRepository.save(userCreateRequest.toEntity());
	}

	@Transactional
	public boolean verifyEmail(VerificationMail verificationMail) {
		return this.verificationService.verifyEmail(verificationMail);
	}

	@Transactional
	public void sendMail(EmailCodeCreateRequest request) {
		String email = request.getEmail();
		String verificationCode = this.verificationService.createVerificationUser(email);

		VerificationMail verificationMail = VerificationMail.builder()
			.email(email)
			.verificationCode(verificationCode)
			.build();

		this.verificationService.send(verificationMail);
	}

	@Transactional(readOnly = true)
	public boolean checkDuplicateEmail(String email) {
		return this.userSlaveRepository.existsByEmail(email);
	}

	@Transactional(readOnly = true)
	public boolean checkDuplicateNickname(String nickname) {
		return this.userSlaveRepository.existsByNickname(nickname);
	}

	// TODO : 예외처리
	private void validateUserCreateRequest(UserCreateRequest userCreateRequest) {
		UserVerification userVerification = this.verificationService.findByEmail(userCreateRequest.getEmail());
		if (!userVerification.isVerified()) {
			throw new RuntimeException("인증되지 않은 회원입니다.");
		}

		if (checkDuplicateEmail(userCreateRequest.getEmail())) {
			throw new RuntimeException("중복된 이메일은 허용되지 않습니다.");
		}

		if(checkDuplicateNickname(userCreateRequest.getNickname())) {
			throw new RuntimeException("중복된 닉네임은 허용되지 않습니다.");
		}
	}
}
