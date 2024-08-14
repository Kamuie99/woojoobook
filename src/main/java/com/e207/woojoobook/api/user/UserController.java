package com.e207.woojoobook.api.user;

import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.e207.woojoobook.api.user.request.EmailCodeCreateRequest;
import com.e207.woojoobook.api.user.request.LoginRequest;
import com.e207.woojoobook.api.user.request.PasswordUpdateRequest;
import com.e207.woojoobook.api.user.request.UserCreateRequest;
import com.e207.woojoobook.api.user.request.UserDeleteRequest;
import com.e207.woojoobook.api.user.request.UserUpdateRequest;
import com.e207.woojoobook.api.user.request.VerificationMail;
import com.e207.woojoobook.api.user.response.UserCountResponse;
import com.e207.woojoobook.api.user.response.UserInfoResponse;
import com.e207.woojoobook.api.user.response.UserPersonalResponse;
import com.e207.woojoobook.api.user.response.VerifyResponse;
import com.e207.woojoobook.api.user.validator.UserValidator;
import com.e207.woojoobook.global.security.SecurityUtil;
import com.e207.woojoobook.global.security.jwt.JwtProvider;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class UserController {

	private final UserService userService;
	private final UserValidator userValidator;
	private final JwtProvider jwtProvider;

	@GetMapping("/users")
	public ResponseEntity<UserInfoResponse> findUserInfo() {
		UserInfoResponse userInfo = this.userService.findUserInfo();
		return ResponseEntity.ok(userInfo);
	}

	@PostMapping("/users")
	public ResponseEntity<?> createUser(@Valid @RequestBody UserCreateRequest userCreateRequest, Errors errors) {
		this.userValidator.validate(userCreateRequest, errors);

		if (errors.hasErrors()) {
			return new ResponseEntity<>(errors.getAllErrors(), HttpStatus.BAD_REQUEST);
		}

		this.userService.join(userCreateRequest);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PostMapping("/users/emails")
	public ResponseEntity<?> sendVerificationCode(@Valid @RequestBody EmailCodeCreateRequest codeRequest,
		Errors errors) {
		if (errors.hasErrors()) {
			return new ResponseEntity<>(errors.getAllErrors(), HttpStatus.BAD_REQUEST);
		}

		this.userService.sendMail(codeRequest);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PutMapping("/users/emails")
	public ResponseEntity<?> verifyEmail(@Valid @RequestBody VerificationMail verificationMail, Errors errors) {
		if (errors.hasErrors()) {
			return new ResponseEntity<>(errors.getAllErrors(), HttpStatus.BAD_REQUEST);
		}
		boolean verified = this.userService.verifyEmail(verificationMail);

		if (!verified) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@GetMapping("/users/emails/{email}")
	public ResponseEntity<?> checkDuplicateEmail(@PathVariable("email") @Email String email, Errors errors) {
		if (errors.hasErrors()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이메일 형식이 아닙니다,.");
		}

		boolean isDuplicate = this.userService.checkDuplicateEmail(email);

		return ResponseEntity.status(HttpStatus.OK).body(new VerifyResponse(isDuplicate));
	}

	@GetMapping("/users/nicknames/{nickname}")
	public ResponseEntity<?> checkDuplicateNickname(@PathVariable("nickname") String nickname) {
		boolean isDuplicate = this.userService.checkDuplicateNickname(nickname);
		return ResponseEntity.status(HttpStatus.OK).body(new VerifyResponse(isDuplicate));
	}

	@PostMapping("/auth")
	public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, Errors errors) {
		if (errors.hasErrors()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("아이디와 비밀번호는 필수 입력값입니다.");
		}
		Map<String, Boolean> isFirstLogin = this.userService.login(loginRequest);
		return ResponseEntity.status(HttpStatus.OK).headers(createTokenFromAuthentication()).body(isFirstLogin);
	}

	@PutMapping("/users")
	public ResponseEntity<?> update(@Valid @RequestBody UserUpdateRequest userUpdateRequest, Errors errors) {
		if (errors.hasErrors()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("필수 입력 값을 입력해주세요");
		}

		this.userService.update(userUpdateRequest);
		return ResponseEntity.status(HttpStatus.OK).headers(createTokenFromAuthentication()).build();
	}

	@PutMapping("/users/password")
	public ResponseEntity<?> changePassword(@Valid @RequestBody PasswordUpdateRequest passwordUpdateRequest,
		Errors errors) {
		if (errors.hasErrors()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("필수 입력값입니다.");
		}
		if (!passwordUpdateRequest.password().equals(passwordUpdateRequest.passwordConfirm())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("변경할 비밀번호가 일치하지 않습니다.");
		}
		this.userService.updatePassword(passwordUpdateRequest);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/users")
	public ResponseEntity<?> delete(@Valid @RequestBody UserDeleteRequest userDeleteRequest, Errors errors) {
		if (errors.hasErrors()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("필수 입력값입니다.");
		}
		this.userService.deleteUser(userDeleteRequest);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@GetMapping("/users/personal")
	public ResponseEntity<UserPersonalResponse> findUserPersonal() {
		UserPersonalResponse userPersonal = this.userService.findUserPersonal();
		return ResponseEntity.ok(userPersonal);
	}

	@GetMapping("/users/count")
	public ResponseEntity<UserCountResponse> countUsers() {
		Long count = userService.countAllUser();
		return ResponseEntity.ok(new UserCountResponse(count));
	}

	private HttpHeaders createTokenFromAuthentication() {
		String token = this.jwtProvider.createToken(SecurityUtil.getAuthentication());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + token);
		return headers;
	}
}
