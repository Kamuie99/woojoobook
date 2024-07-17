package com.e207.woojoobook.api.user;

import org.springframework.stereotype.Service;

import com.e207.woojoobook.domain.user.UserVerification;
import com.e207.woojoobook.domain.user.UserVerificationRepository;
import com.e207.woojoobook.domain.user.VerificationCodeUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

	private final UserVerificationRepository userVerificationRepository;
	private final UserMasterRepository userMasterRepository;

	public void join(UserCreateRequest userCreateRequest) {
		UserVerification userVerification = this.userVerificationRepository.findById(userCreateRequest.getEmail())
			.orElseThrow(() -> new RuntimeException("존재하지 않는 email"));
		if(!userVerification.isVerified()) {
			throw new RuntimeException("인증되지 않은 회원");
		}


	}



	public boolean verifyMember(String email, String code) {
		UserVerification userVerification = this.userVerificationRepository.findById(email)
			.orElseThrow(() -> new RuntimeException("존재하지 않는 email"));
		userVerification.verify(code);

		UserVerification save = this.userVerificationRepository.save(userVerification);

		return save.isVerified();
	}

	public void createVerificationMember(String email) {
		UserVerification userVerification = UserVerification.builder()
			.email(email)
			.verificationCode(VerificationCodeUtil.generate())
			.isVerified(false)
			.build();

		this.userVerificationRepository.save(userVerification);
	}
}
