package com.e207.woojoobook.api.user.request;

import java.util.Objects;

public record UserUpdateRequest(String nickname, String areaCode) {
	public UserUpdateRequest {
		Objects.requireNonNull(nickname);
		Objects.requireNonNull(areaCode);
	}
}
