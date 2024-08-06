package com.e207.woojoobook.global.helper;

import org.springframework.stereotype.Component;

import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.user.UserRepository;
import com.e207.woojoobook.global.exception.ErrorCode;
import com.e207.woojoobook.global.exception.ErrorException;
import com.e207.woojoobook.global.security.SecurityUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class UserHelper {

	private final UserRepository userRepository;

	public User findCurrentUser() {
		Long currentUsername = SecurityUtil.getCurrentUsername();

		return this.userRepository.findById(currentUsername)
			.orElseThrow(() -> new ErrorException(ErrorCode.UserNotFound));
	}

	public User findById(Long id) {
		return this.userRepository.findById(id)
				.orElseThrow(() -> new ErrorException(ErrorCode.UserNotFound));
	}
}
