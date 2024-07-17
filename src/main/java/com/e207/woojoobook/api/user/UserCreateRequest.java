package com.e207.woojoobook.api.user;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserCreateRequest {
	@Email
	private String email;
	private String password;
	private String passwordConfirm;
	private String nickname;
	private String areaCode;
}
