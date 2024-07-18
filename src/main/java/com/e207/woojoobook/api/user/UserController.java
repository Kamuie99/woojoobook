package com.e207.woojoobook.api.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.e207.woojoobook.api.mail.VerificationMail;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class UserController {

	private final UserService userService;
	private final UserValidator userValidator;

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
}
