package com.e207.woojoobook.api.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class UserController {

	private final UserService userService;

	@PostMapping("/users")
	public ResponseEntity createUser(@RequestBody UserCreateRequest userCreateRequest) {
		userService.join(userCreateRequest);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}


}
