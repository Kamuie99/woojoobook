package com.e207.woojoobook.domain.user.experience;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.user.UserRepository;

@DataJpaTest
class ExperienceRepositoryTest {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ExperienceRepository experienceRepository;
	@Autowired
	private ExperienceViewRepository experienceViewRepository;

	@DisplayName("포인트를 조회하는 뷰로 포인트를 조회한다")
	@Test
	void readPoint() {
		// given
		User user = User.builder()
			.build();
		user = this.userRepository.save(user);

		for (int i = 0; i < 5; i++) {
			Experience experience = Experience.builder()
				.user(user)
				.history(ExperienceHistory.ATTENDANCE)
				.amount(ExperienceHistory.ATTENDANCE.getAmount())
				.build();
			this.experienceRepository.save(experience);
		}

		// when
		Optional<ExperienceView> byId = this.experienceViewRepository.findById(user.getId());

		// then
		assertTrue(byId.isPresent());
		assertEquals(byId.get().getTotalExperience(), 500);
	}
}