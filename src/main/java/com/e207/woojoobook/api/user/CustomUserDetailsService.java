package com.e207.woojoobook.api.user;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.e207.woojoobook.domain.user.UserRepository;
import com.e207.woojoobook.global.exception.ErrorCode;
import com.e207.woojoobook.global.exception.ErrorException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		com.e207.woojoobook.domain.user.User user = this.userRepository.findByEmail(username)
			.orElseThrow(() -> new ErrorException(ErrorCode.NotFound));
		return User.builder()
			.username(String.valueOf(user.getId()))
			.password(user.getPassword())
			.authorities("ROLE_USER")
			.build();
	}
}
