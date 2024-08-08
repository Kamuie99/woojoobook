package com.e207.woojoobook.api.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.Map;
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

import com.e207.woojoobook.api.user.request.LoginRequest;
import com.e207.woojoobook.api.user.request.UserDeleteRequest;
import com.e207.woojoobook.api.user.response.UserInfoResponse;
import com.e207.woojoobook.domain.chatroom.ChatRoom;
import com.e207.woojoobook.domain.chatroom.ChatRoomRepository;
import com.e207.woojoobook.domain.rental.Rental;
import com.e207.woojoobook.domain.rental.RentalRepository;
import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.user.UserRepository;
import com.e207.woojoobook.domain.user.experience.ExperienceHistory;
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
	private UserPersonalFacade userPersonalFacade;
	@Autowired
	private ChatRoomRepository chatRoomRepository;
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
		User savedUser = this.userRepository.save(createUser(password));
		Userbook savedUserbook = this.userbookRepository.save(createUserbook(savedUser));
		Point savedPoint = createPoint(savedUser);
		savedUser.getPoints().add(savedPoint);
		savedPoint = this.pointRepository.save(savedPoint);
		Rental savedRental = createRental(savedUserbook, savedUser);
		savedRental = this.rentalRepository.save(savedRental);
		User testUser = this.userRepository.save(createUser("test"));
		ChatRoom chatRoom = this.chatRoomRepository.save(ChatRoom.builder()
			.sender(savedUser)
			.receiver(testUser)
			.build());

		given(this.userHelper.findCurrentUser()).willReturn(savedUser);
		given(authenticationManagerBuilder.getObject()).willReturn(authenticationManager);
		given(authenticationManager.authenticate(any())).willReturn(authentication);
		given(authentication.isAuthenticated()).willReturn(true);

		assertNotNull(savedUserbook.getUser());
		Long userId = savedUserbook.getUser().getId();
		Long pointId = savedPoint.getId();
		Long rentalId = savedRental.getId();
		Long chatRoomId = chatRoom.getId();

		// when
		this.userService.deleteUser(new UserDeleteRequest(password));
		em.flush();

		// then
		assertNull(savedUserbook.getUser());
		assertTrue(this.userRepository.findById(userId).isEmpty());
		assertTrue(this.pointRepository.findById(pointId).isEmpty());
		Optional<Rental> rental = this.rentalRepository.findById(rentalId);
		Optional<ChatRoom> byId = this.chatRoomRepository.findById(chatRoomId);
		assertTrue(byId.isPresent());
		assertTrue(rental.isPresent());
		assertEquals(byId.get().getSender(), null);
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

	@DisplayName("회원의 접속 날짜가 현재 날짜 이전이라면 포인트, 경험치가 증가한다")
	@Test
	void firstLogin() {
		// given
		String email = "test@test.com";
		String password = "password";
		User user = createUser(password);
		LocalDate pastDate = LocalDate.of(2024, 01, 01);
		user.updateLoginDate(pastDate);
		User savedUser = this.userRepository.save(user);

		given(authenticationManagerBuilder.getObject()).willReturn(authenticationManager);
		given(authenticationManager.authenticate(any())).willReturn(authentication);
		given(authentication.isAuthenticated()).willReturn(true);
		given(this.userHelper.findCurrentUser()).willReturn(savedUser);

		assertEquals(this.userPersonalFacade.getUserPoints(savedUser.getId()), 0);
		assertEquals(this.userPersonalFacade.getUserExperience(savedUser.getId()), 0);

		// when
		Map<String, Boolean> result = this.userService.login(new LoginRequest(email, password));

		// then
		int pointAmount = PointHistory.ATTENDANCE.getAmount();
		int experienceAmount = ExperienceHistory.ATTENDANCE.getAmount();

		assertEquals(result.get("isFirstLogin"), true);
		assertEquals(this.userPersonalFacade.getUserPoints(savedUser.getId()), pointAmount);
		assertEquals(this.userPersonalFacade.getUserExperience(savedUser.getId()), experienceAmount);
	}

	@DisplayName("회원의 최근 접속 날짜가 현재 날짜라면 포인트, 경험치가 증가하지 않는다")
	@Test
	void notFirstLogin() {
		// given
		String email = "test@test.com";
		String password = "password";
		User user = createUser(password);
		LocalDate currentDate = LocalDate.now();
		user.updateLoginDate(currentDate);
		User savedUser = this.userRepository.save(user);

		given(authenticationManagerBuilder.getObject()).willReturn(authenticationManager);
		given(authenticationManager.authenticate(any())).willReturn(authentication);
		given(authentication.isAuthenticated()).willReturn(true);
		given(this.userHelper.findCurrentUser()).willReturn(savedUser);

		assertEquals(this.userPersonalFacade.getUserPoints(savedUser.getId()), 0);
		assertEquals(this.userPersonalFacade.getUserExperience(savedUser.getId()), 0);

		// when
		Map<String, Boolean> result = this.userService.login(new LoginRequest(email, password));

		// then
		assertEquals(result.get("isFirstLogin"), false);
		assertEquals(this.userPersonalFacade.getUserPoints(savedUser.getId()), 0);
		assertEquals(this.userPersonalFacade.getUserExperience(savedUser.getId()), 0);
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