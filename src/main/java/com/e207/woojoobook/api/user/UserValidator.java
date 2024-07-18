package com.e207.woojoobook.api.user;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.e207.woojoobook.api.user.request.UserCreateRequest;

@Component
public class UserValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return UserCreateRequest.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		UserCreateRequest request = (UserCreateRequest)target;

		if (!request.getPassword().equals(request.getPasswordConfirm())) {
			errors.rejectValue("passwordConfirm", "비밀번호가 일치하지 않습니다.");
		}
	}
}
