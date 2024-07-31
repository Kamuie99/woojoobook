package com.e207.woojoobook.api.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;

import com.e207.woojoobook.api.user.request.UserDeleteRequest;
import com.e207.woojoobook.api.user.response.UserInfoResponse;
import com.e207.woojoobook.domain.rental.Rental;
import com.e207.woojoobook.domain.rental.RentalRepository;
import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.user.UserRepository;
import com.e207.woojoobook.domain.user.point.Point;
import com.e207.woojoobook.domain.user.point.PointHistory;
import com.e207.woojoobook.domain.user.point.PointRepository;
import com.e207.woojoobook.domain.userbook.Userbook;
import com.e207.woojoobook.domain.userbook.UserbookRepository;
import com.e207.woojoobook.global.helper.UserHelper;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@SpringBootTest
class UserServiceTest {

	@Autowired
	private PointRepository pointRepository;
	@Autowired
	private RentalRepository rentalRepository;
	@Autowired
	private UserService userService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserbookRepository userbookRepository;
	@Autowired
	private EntityManager em;
	@MockBean
	private UserHelper userHelper;
	@MockBean
	private AuthenticationManagerBuilder authenticationManagerBuilder;
	@Mock
	private AuthenticationManager authenticationManager;
	@Mock
	private Authentication authentication;


	@DisplayName("회원 탈퇴를 하면, 참조하고 있던 객체가 NULL로 처리되고 사용자의 정보는 삭제된다")
	@Transactional
	@Test
	void deleteUser() {
		// given
		String password = "password";
		User savedUser = createUser(password);
		savedUser = this.userRepository.save(savedUser);
		Userbook savedUserbook = createUserbook(savedUser);
		savedUserbook = this.userbookRepository.save(savedUserbook);
		Point savedPoint = createPoint(savedUser);
		savedUser.getPoints().add(savedPoint);
		savedPoint = this.pointRepository.save(savedPoint);
		Rental savedRental = createRental(savedUserbook, savedUser);
		savedRental = this.rentalRepository.save(savedRental);

		given(this.userHelper.findCurrentUser()).willReturn(savedUser);
		given(authenticationManagerBuilder.getObject()).willReturn(authenticationManager);
		given(authenticationManager.authenticate(any())).willReturn(authentication);
		given(authentication.isAuthenticated()).willReturn(true);

		assertNotNull(savedUserbook.getUser());
		Long userId = savedUserbook.getUser().getId();
		Long pointId = savedPoint.getId();
		Long rentalId = savedRental.getId();

		// when
		this.userService.deleteUser(new UserDeleteRequest(password));
		em.flush();

		// then
		assertNull(savedUserbook.getUser());
		assertTrue(this.userRepository.findById(userId).isEmpty());
		assertTrue(this.pointRepository.findById(pointId).isEmpty());
		Optional<Rental> rental = this.rentalRepository.findById(rentalId);
		assertTrue(rental.isPresent());
		assertNull(rental.get().getUser());
	}

	@DisplayName("회원의 포인트를 조회한다")
	@Test
	void findUserPoint() {
		// given
		User user = this.userRepository.save(createUser("password"));
		PointHistory history = PointHistory.BOOK_EXCHANGE;
		int expectedPoints = history.getAmount();
		this.pointRepository.save(createPoint(user, history));

		given(this.userHelper.findCurrentUser()).willReturn(user);

		// when
		int amount = this.userService.retrievePoint();

		// then
		assertEquals(amount, expectedPoints);
	}

	@DisplayName("회원의 기본 정보를 조회한다.")
	@Test
	void findUserInfo() {
		// given
		String email = "test@test.com";
		String nickname = "nickname";
		String area = "area";
		User user = cretaeInfoUser(email, nickname, area);
		User save = this.userRepository.save(user);

		given(this.userHelper.findCurrentUser()).willReturn(save);

		// when
		UserInfoResponse userInfo = this.userService.findUserInfo();

		// then
		assertNotNull(userInfo);
		assertEquals(userInfo.email(), email);
		assertEquals(userInfo.nickname(), nickname);
		assertEquals(userInfo.areaCode(), area);

	}

	private static User cretaeInfoUser(String email, String nickname, String area) {
		User user = User.builder()
			.email(email)
			.password("test")
			.nickname(nickname)
			.areaCode(area)
			.build();
		return user;
	}

	private static Rental createRental(Userbook userbook, User user) {
		Rental rental = Rental.builder()
			.userbook(userbook)
			.user(user)
			.build();
		return rental;
	}

	private static Point createPoint(User user) {
		Point point = Point.builder()
			.user(user)
			.history(PointHistory.ATTENDANCE)
			.amount(PointHistory.ATTENDANCE.getAmount())
			.build();
		return point;
	}

	private static Point createPoint(User user, PointHistory history) {
		Point point = Point.builder()
				.user(user)
				.history(history)
				.amount(history.getAmount())
				.build();
		return point;
	}

	private static Userbook createUserbook(User user) {
		Userbook userbook = Userbook.builder()
			.user(user)
			.build();
		return userbook;
	}

	private static User createUser(String password) {
		User user = User.builder()
			.email("test@test.com")
			.password(password)
			.build();
		return user;
	}
}