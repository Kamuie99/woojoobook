package com.e207.woojoobook.api.user.response;

import com.e207.woojoobook.domain.user.User;

import lombok.Builder;

@Builder
public record UserResponse(Long id, String nickname, String email) {

	public static UserResponse of(User user) {
		return new UserResponse(user.getId(), user.getNickname(), user.getEmail());
	}
}
