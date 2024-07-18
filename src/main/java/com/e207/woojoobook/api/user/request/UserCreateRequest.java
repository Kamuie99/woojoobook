package com.e207.woojoobook.api.user.request;


import com.e207.woojoobook.domain.user.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserCreateRequest {
	@Email
	private String email;
	@Size(min = 8)
	private String password;
	@Size(min = 8)
	private String passwordConfirm;
	@NotBlank
	private String nickname;
	@NotBlank
	private String areaCode;

	public User toEntity() {
		return User.builder()
			.email(this.email)
			.password(this.password)
			.nickname(this.nickname)
			.areaCode(this.areaCode)
			.build();
	}

	public void encode(String password) {
		this.password = password;
	}
}
