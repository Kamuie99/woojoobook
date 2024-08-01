package com.e207.woojoobook.api.user;

import java.time.LocalDate;
import java.util.Map;

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
import com.e207.woojoobook.api.userbook.event.ExperienceEvent;
import com.e207.woojoobook.api.userbook.event.PointEvent;
import com.e207.woojoobook.api.userbook.event.UserDeleteEvent;
import com.e207.woojoobook.api.verification.VerificationService;
import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.user.UserRepository;
import com.e207.woojoobook.domain.user.UserVerification;
import com.e207.woojoobook.domain.user.experience.ExperienceHistory;
import com.e207.woojoobook.domain.user.point.PointHistory;
import com.e207.woojoobook.global.exception.ErrorCode;
import com.e207.woojoobook.global.exception.ErrorException;
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

	@Transactional(readOnly = true)
	public Map<String, Boolean> login(LoginRequest loginRequest) {
		var authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.email(),
			loginRequest.password());
		Authentication authenticate = this.authenticationManagerBuilder.getObject().authenticate(authenticationToken);
		if (!authenticate.isAuthenticated()) {
			throw new ErrorException(ErrorCode.InvalidPassword);
		}
		SecurityUtil.setAuthentication(authenticate);

		User currentUser = this.userHelper.findCurrentUser();
		return checkIsFirstLogin(currentUser);
	}

	@Transactional
	public void update(UserUpdateRequest userUpdateRequest) {
		User user = this.userHelper.findCurrentUser();
		user.update(userUpdateRequest.nickname(), userUpdateRequest.areaCode());
	}


	@Transactional
	public void updatePassword(PasswordUpdateRequest passwordUpdateRequest) {
		User user = this.userHelper.findCurrentUser();

		checkPassword(user.getId(), passwordUpdateRequest.curPassword());
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
		return userRepository.findById(id).orElseThrow(() -> new ErrorException(ErrorCode.UserNotFound));
	}

	private Map<String, Boolean> checkIsFirstLogin(User user) {
		if(user.getLastLoginDate().isBefore(LocalDate.now())){
			user.updateLoginDate(LocalDate.now());
			this.eventPublisher.publishEvent(new ExperienceEvent(user, ExperienceHistory.ATTENDANCE));
			this.eventPublisher.publishEvent(new PointEvent(user, PointHistory.ATTENDANCE));
			return Map.of("isFirstLogin", true);
		}
		return Map.of("isFirstLogin", false);
	}

	private void validateUserCreateRequest(UserCreateRequest userCreateRequest) {
		UserVerification userVerification = this.verificationService.findByEmail(userCreateRequest.getEmail());
		if (!userVerification.isVerified()) {
			throw new ErrorException(ErrorCode.ForbiddenError);
		}

		if (checkDuplicateEmail(userCreateRequest.getEmail())) {
			throw new ErrorException(ErrorCode.NotAcceptDuplicate);
		}

		if (checkDuplicateNickname(userCreateRequest.getNickname())) {
			throw new ErrorException(ErrorCode.NotAcceptDuplicate);
		}
	}

	private void checkPassword(Long id, String password) {
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(id, password);
		Authentication authenticate = this.authenticationManagerBuilder.getObject().authenticate(token);
		if (!authenticate.isAuthenticated()) {
			throw new ErrorException(ErrorCode.InvalidPassword);
		}
	}
}
