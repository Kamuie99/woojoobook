package com.e207.woojoobook.api.user.request;

import java.util.Objects;

import lombok.Builder;

@Builder
public record LoginRequest (String email, String password) {
	public LoginRequest {
		Objects.requireNonNull(email);
		Objects.requireNonNull(password);
	}
}
