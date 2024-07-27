package com.e207.woojoobook.api.user.point;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import com.e207.woojoobook.domain.user.point.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.user.UserRepository;
import com.e207.woojoobook.global.util.DynamicQueryHelper;

@Import({DynamicQueryHelper.class})
@DataJpaTest
class PointRepositoryTest {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PointRepository pointRepository;
	@Autowired
	private PointViewRepository pointViewRepository;

	@DisplayName("포인트를 조회하는 뷰로 포인트를 조회한다")
	@Test
	void readPoint() {
		// given
		User user = User.builder()
			.build();
		user = this.userRepository.save(user);

		for(int i = 0; i < 5; i++) {
			Point point = Point.builder()
				.user(user)
				.history(PointHistory.ATTENDANCE)
				.amount(PointHistory.ATTENDANCE.getAmount())
				.build();
			this.pointRepository.save(point);
		}

		// when
		Optional<PointView> byId = this.pointViewRepository.findById(user.getId());

		// then
		assertTrue(byId.isPresent());
		assertEquals(byId.get().getTotalPoint(), 50);
	}

}