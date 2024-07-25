package com.e207.woojoobook.global.util;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.user.UserRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class InitDataGenerator {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final PlatformTransactionManager transactionManager;

	@PostConstruct
	public void initUser() {
		TransactionStatus ts = transactionManager.getTransaction(new DefaultTransactionDefinition());
		User user = User.builder()
			.email("test@test.com")
			.password(passwordEncoder.encode("1234"))
			.nickname("testUser")
			.build();
		userRepository.save(user);
		transactionManager.commit(ts);
	}
}
