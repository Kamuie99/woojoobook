package com.e207.woojoobook.api.user;

import java.util.regex.Pattern;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.e207.woojoobook.api.user.request.EmailCodeCreateRequest;
import com.e207.woojoobook.api.user.request.LoginRequest;
import com.e207.woojoobook.api.user.request.UserCreateRequest;
import com.e207.woojoobook.api.user.request.UserUpdateRequest;
import com.e207.woojoobook.api.verification.request.VerificationMail;
import com.e207.woojoobook.global.security.SecurityUtil;
import com.e207.woojoobook.global.security.jwt.JwtProvider;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class UserController {

	private static final String EMAIL_PATTERN =
		"^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
	private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

	private final UserService userService;
	private final UserValidator userValidator;
	private final JwtProvider jwtProvider;

	@PostMapping("/users")
	public ResponseEntity<?> createUser(@Valid @RequestBody UserCreateRequest userCreateRequest,
		Errors errors) {
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
	public ResponseEntity<?> checkDuplicateEmail(@PathVariable("email") String email) {
		if(!pattern.matcher(email).matches()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이메일 형식이 아닙니다,.");
		}

		boolean isDuplicate = this.userService.checkDuplicateEmail(email);

		if(!isDuplicate) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}

		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@GetMapping("/users/nicknames/{nickname}")
	public ResponseEntity<?> checkDuplicateNickname(@PathVariable("nickname") String nickname) {
		boolean isDuplicate = this.userService.checkDuplicateNickname(nickname);

		if(!isDuplicate) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}

		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@PostMapping("/auth")
	public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest,
		Errors errors) {
		if(errors.hasErrors()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("아이디와 비밀번호는 필수 입력값입니다.");
		}
		this.userService.login(loginRequest);
		return ResponseEntity.status(HttpStatus.OK).headers(createTokenFromAuthentication()).build();
	}

	@PutMapping("/users")
	public ResponseEntity<?> update(@Valid @RequestBody UserUpdateRequest userUpdateRequest,Errors errors) {
		if(errors.hasErrors()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("필수 입력 값을 입력해주세요");
		}

		this.userService.update(userUpdateRequest);
		return ResponseEntity.status(HttpStatus.OK).headers(createTokenFromAuthentication()).build();
	}

	@PutMapping("/users/password")
	public ResponseEntity<?> changePassword(@Valid @RequestBody PasswordUpdateRequest passwordUpdateRequest,
		Errors errors){
		if(errors.hasErrors()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("필수 입력값입니다.");
		}
		if(!passwordUpdateRequest.password().equals(passwordUpdateRequest.passwordConfirm())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("변경할 비밀번호가 일치하지 않습니다.");
		}
		this.userService.updatePassword(passwordUpdateRequest);
		return ResponseEntity.ok().build();
	}

	private HttpHeaders createTokenFromAuthentication() {
		String token = this.jwtProvider.createToken(SecurityUtil.getAuthentication());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + token);
		return headers;
	}
}
