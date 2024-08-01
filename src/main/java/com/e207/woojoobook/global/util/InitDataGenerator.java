package com.e207.woojoobook.global.util;

import java.util.stream.IntStream;

import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.user.UserRepository;
import com.e207.woojoobook.domain.user.experience.Experience;
import com.e207.woojoobook.domain.user.experience.ExperienceHistory;
import com.e207.woojoobook.domain.user.experience.ExperienceRepository;
import com.e207.woojoobook.domain.user.point.Point;
import com.e207.woojoobook.domain.user.point.PointHistory;
import com.e207.woojoobook.domain.user.point.PointRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Profile("local")
@Component
public class InitDataGenerator {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final PointRepository pointRepository;
	private final ExperienceRepository experienceRepository;
	private final PlatformTransactionManager transactionManager;

	@PostConstruct
	public void initUser() {
		TransactionStatus ts = transactionManager.getTransaction(new DefaultTransactionDefinition());
		User user = User.builder()
			.email("test@test.com")
			.password(passwordEncoder.encode("1234"))
			.nickname("testUser")
			.areaCode("1111051500")
			.build();
		User save = userRepository.save(user);
		transactionManager.commit(ts);

		IntStream.range(0, 10).forEach(i -> {
			Point point = Point.builder()
				.user(save)
				.history(PointHistory.BOOK_REGISTER)
				.build();
			this.pointRepository.save(point);

			Experience experience = Experience.builder()
				.user(save)
				.history(ExperienceHistory.BOOK_REGISTER)
				.build();
			this.experienceRepository.save(experience);
		});
	}
}
