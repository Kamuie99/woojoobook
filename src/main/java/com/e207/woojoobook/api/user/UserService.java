package com.e207.woojoobook.api.user;

import java.util.Objects;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.e207.woojoobook.api.user.request.EmailCodeCreateRequest;
import com.e207.woojoobook.api.user.request.LoginRequest;
import com.e207.woojoobook.api.user.request.PasswordUpdateRequest;
import com.e207.woojoobook.api.user.request.UserCreateRequest;
import com.e207.woojoobook.api.user.request.UserDeleteRequest;
import com.e207.woojoobook.api.user.request.UserUpdateRequest;
import com.e207.woojoobook.api.user.request.VerificationMail;
import com.e207.woojoobook.api.user.response.UserInfoResponse;
import com.e207.woojoobook.api.userbook.event.UserDeleteEvent;
import com.e207.woojoobook.api.verification.VerificationService;
import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.user.UserRepository;
import com.e207.woojoobook.domain.user.UserVerification;
import com.e207.woojoobook.global.helper.UserHelper;
import com.e207.woojoobook.global.security.SecurityUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {
	private final UserRepository userRepository;
	private final UserHelper userHelper;
	private final PasswordEncoder passwordEncoder;
	private final VerificationService verificationService;
	private final UserPersonalFacade userPersonalFacade;
	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	private final ApplicationEventPublisher eventPublisher;


	@Transactional(readOnly = false)
	public UserInfoResponse findUserInfo() {
		User currentUser = this.userHelper.findCurrentUser();

		return UserInfoResponse.builder()
			.id(currentUser.getId())
			.email(currentUser.getEmail())
			.nickname(currentUser.getNickname())
			.areaCode(currentUser.getAreaCode())
			.build();
	}

	@Transactional
	public void join(UserCreateRequest userCreateRequest) {
		validateUserCreateRequest(userCreateRequest);

		userCreateRequest.encode(this.passwordEncoder.encode(userCreateRequest.getPassword()));

		this.userRepository.save(userCreateRequest.toEntity());
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
		return this.userRepository.existsByEmail(email);
	}

	@Transactional(readOnly = true)
	public boolean checkDuplicateNickname(String nickname) {
		return this.userRepository.existsByNickname(nickname);
	}

	// TODO : 예외처리
	@Transactional(readOnly = true)
	public void login(LoginRequest loginRequest) {
		var authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.email(),
			loginRequest.password());
		Authentication authenticate = this.authenticationManagerBuilder.getObject().authenticate(authenticationToken);
		if (!authenticate.isAuthenticated()) {
			throw new RuntimeException("비밀번호가 일치하지 않습니다.");
		}
		SecurityUtil.setAuthentication(authenticate);
	}

	// TODO : 예외처리
	@Transactional
	public void update(UserUpdateRequest userUpdateRequest) {
		User user = this.userHelper.findCurrentUser();
		user.update(userUpdateRequest.nickname(), userUpdateRequest.areaCode());
	}


	// TODO : 예외처리
	@Transactional
	public void updatePassword(PasswordUpdateRequest passwordUpdateRequest) {
		User user = this.userRepository.findById(Objects.requireNonNull(SecurityUtil.getCurrentUsername()))
			.orElseThrow(() -> new RuntimeException("인증 정보가 없습니다."));

		if (!passwordEncoder.matches(passwordUpdateRequest.curPassword(), user.getPassword())) {
			throw new RuntimeException("비밀번호가 일치하지 않습니다.");
		}
		user.updatePassword(passwordEncoder.encode(passwordUpdateRequest.password()));
	}

	@Transactional
	public void deleteUser(UserDeleteRequest userDeleteRequest) {
		User user = this.userHelper.findCurrentUser();
		checkPassword(user.getId(), userDeleteRequest.password());
		this.eventPublisher.publishEvent(new UserDeleteEvent(user));
	}

	@Transactional(readOnly = true)
	public int retrievePoint() {
		User user = this.userHelper.findCurrentUser();
		return this.userPersonalFacade.getUserPoints(user.getId());
	}

	public User findDomainById(Long id) {
		return userRepository.findById(id).orElseThrow(() -> new RuntimeException("user not found"));
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

		if (checkDuplicateNickname(userCreateRequest.getNickname())) {
			throw new RuntimeException("중복된 닉네임은 허용되지 않습니다.");
		}
	}

	private void checkPassword(Long id, String password) {
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(id, password);
		Authentication authenticate = this.authenticationManagerBuilder.getObject().authenticate(token);
		if (!authenticate.isAuthenticated()) {
			throw new RuntimeException("비밀번호가 일치하지 않습니다.");
		}
	}
}
