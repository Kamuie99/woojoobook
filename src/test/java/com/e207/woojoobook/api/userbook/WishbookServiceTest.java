package com.e207.woojoobook.api.userbook;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;

import com.e207.woojoobook.api.userbook.response.WishbookResponse;
import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.user.UserRepository;
import com.e207.woojoobook.domain.userbook.RegisterType;
import com.e207.woojoobook.domain.userbook.TradeStatus;
import com.e207.woojoobook.domain.userbook.Userbook;
import com.e207.woojoobook.domain.userbook.UserbookRepository;
import com.e207.woojoobook.domain.userbook.Wishbook;
import com.e207.woojoobook.domain.userbook.WishbookRepository;
import com.e207.woojoobook.global.exception.ErrorException;
import com.e207.woojoobook.global.helper.UserHelper;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class WishbookServiceTest {

	@Autowired
	private WishbookService wishBookService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserbookRepository userbookRepository;
	@Autowired
	private WishbookRepository wishBookRepository;
	@MockBean
	private JavaMailSender mailSender;
	@MockBean
	private UserHelper userHelper;

	private Userbook userbook;
	private User user;
	private User owner;
	private User wishUser;

	@BeforeEach
	void setUp() {
		// 유저 생성
		User user = User.builder()
			.email("test@naver.com")
			.nickname("name")
			.password("password")
			.areaCode("areaCode")
			.build();
		this.user = this.userRepository.save(user);

		user = User.builder()
			.email("test123@naver.com")
			.nickname("name2")
			.password("password")
			.areaCode("areaCode")
			.build();
		this.owner = this.userRepository.save(user);

		user = User.builder()
			.email("test2323@naver.com")
			.nickname("name3")
			.password("password")
			.areaCode("areaCode")
			.build();
		this.wishUser = this.userRepository.save(user);

		// 사용자 도서
		Userbook build = Userbook.builder()
			.registerType(RegisterType.RENTAL_EXCHANGE)
			.tradeStatus(TradeStatus.UNAVAILABLE)
			.user(owner)
			.build();
		this.userbook = this.userbookRepository.save(build);

		Wishbook wishBook = Wishbook.builder()
			.user(wishUser)
			.userbook(userbook)
			.build();
		Wishbook save = this.wishBookRepository.save(wishBook);
		userbook.getWishbooks().add(save);
		this.userbook = this.userbookRepository.save(userbook);
	}

	@DisplayName("관심 등록 되어있지 않은 사용자도서 관심 등록 요청")
	@Test
	void wishUpdate() {
		// given
		given(this.userHelper.findCurrentUser()).willReturn(user);
		Long userbookId = userbook.getId();
		Long userId = this.user.getId();
		boolean wished = false;

		// when
		WishbookResponse wishBookResponse = this
			.wishBookService.updateWishbook(userbookId, wished);

		// then
		assertNotNull(wishBookResponse);

		assertTrue(wishBookResponse.wished());

		Optional<Wishbook> byId = this
			.wishBookRepository.findWithUserbookByUserIdAndUserbookId(userId, userbookId);
		assertTrue(byId.isPresent());

		Wishbook updatedWishbook = byId.get();
		assertEquals(updatedWishbook.getUserbook().getId(), userbookId);
		assertEquals(updatedWishbook.getUser().getId(), user.getId());
	}

	@DisplayName("관심 등록된 사용자도서 관심 등록 취소 요청")
	@Test
	void wishDeleteUpdate() {
		// given
		given(this.userHelper.findCurrentUser()).willReturn(wishUser);
		Long userbookId = userbook.getId();
		Long userId = this.wishUser.getId();
		boolean wished = true;

		// when
		WishbookResponse wishBookResponse = this
			.wishBookService.updateWishbook(userbookId, wished);

		// then
		assertNotNull(wishBookResponse);

		assertFalse(wishBookResponse.wished());

		Optional<Wishbook> byId = this
			.wishBookRepository.findWithUserbookByUserIdAndUserbookId(userId, userbookId);
		assertFalse(byId.isPresent());
	}

	@DisplayName("존재하지 않는 도서는 관심등록 불가능")
	@Test
	void wishUpdate_doseNotExist_fail() {
		// given
		given(this.userHelper.findCurrentUser()).willReturn(user);
		Long invalidUserbookId = 213123L;
		String expectedMessage = "사용자 도서가 없습니다.";

		// expected
		assertThrows(ErrorException.class,
			() -> this.wishBookService.updateWishbook(invalidUserbookId, false));
	}

	@DisplayName("이미 관심 등록된 책에 관심 등록 요청")
	@Test
	void wishUpdate_alreadyWished_fail() {
		// given
		given(this.userHelper.findCurrentUser()).willReturn(wishUser);
		Long userbookId = userbook.getId();
		boolean wished = false;
		String expectedMessage = "이미 관심 목록에 추가된 책입니다.";

		// expected
		assertThrows(ErrorException.class,
			() -> this.wishBookService.updateWishbook(userbookId, wished));
	}

	@DisplayName("관심 목록에 존재하지 않는 책에 관심 등록 취소 요청")
	@Test
	void wishUpdate_doseNotExistWishList_fail() {
		// given
		given(this.userHelper.findCurrentUser()).willReturn(user);
		Long userbookId = userbook.getId();
		boolean wished = true;
		String expectedMessage = "관심 목록에 존재하지 않는 책입니다.";

		// expected
		assertThrows(ErrorException.class,
			() -> this.wishBookService.updateWishbook(userbookId, wished));

	}
}
