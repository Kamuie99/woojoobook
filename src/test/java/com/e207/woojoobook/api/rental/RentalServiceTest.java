package com.e207.woojoobook.api.rental;

import static com.e207.woojoobook.domain.rental.RentalStatus.*;
import static com.e207.woojoobook.domain.rental.RentalUserCondition.*;
import static com.e207.woojoobook.domain.userbook.QualityStatus.*;
import static com.e207.woojoobook.domain.userbook.RegisterType.*;
import static com.e207.woojoobook.domain.userbook.TradeStatus.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.mail.javamail.JavaMailSender;

import com.e207.woojoobook.api.extension.ExtensionService;
import com.e207.woojoobook.api.rental.request.RentalFindCondition;
import com.e207.woojoobook.api.rental.request.RentalOfferRespondRequest;
import com.e207.woojoobook.api.rental.response.RentalOfferResponse;
import com.e207.woojoobook.api.rental.response.RentalResponse;
import com.e207.woojoobook.api.userbook.response.UserbookResponse;
import com.e207.woojoobook.domain.book.Book;
import com.e207.woojoobook.domain.book.BookRepository;
import com.e207.woojoobook.domain.extension.Extension;
import com.e207.woojoobook.domain.extension.ExtensionRepository;
import com.e207.woojoobook.domain.extension.ExtensionStatus;
import com.e207.woojoobook.domain.rental.Rental;
import com.e207.woojoobook.domain.rental.RentalRepository;
import com.e207.woojoobook.domain.rental.RentalStatus;
import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.user.UserRepository;
import com.e207.woojoobook.domain.user.point.Point;
import com.e207.woojoobook.domain.user.point.PointHistory;
import com.e207.woojoobook.domain.user.point.PointRepository;
import com.e207.woojoobook.domain.userbook.RegisterType;
import com.e207.woojoobook.domain.userbook.TradeStatus;
import com.e207.woojoobook.domain.userbook.Userbook;
import com.e207.woojoobook.domain.userbook.UserbookRepository;
import com.e207.woojoobook.domain.userbook.WishbookRepository;
import com.e207.woojoobook.global.exception.ErrorCode;
import com.e207.woojoobook.global.exception.ErrorException;
import com.e207.woojoobook.global.helper.UserHelper;

import jakarta.mail.internet.MimeMessage;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class RentalServiceTest {

	@Autowired
	private RentalService rentalService;
	@Autowired
	private BookRepository bookRepository;
	@Autowired
	private UserbookRepository userbookRepository;
	@Autowired
	private RentalRepository rentalRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private WishbookRepository wishBookRepository;
	@Autowired
	private ExtensionService extensionService;
	@Autowired
	private ExtensionRepository extensionRepository;
	@Autowired
	private PointRepository pointRepository;
	@MockBean
	private JavaMailSender mailSender;
	@MockBean
	private UserHelper userHelper;

	private Userbook userbook;
	private User user;
	private User owner;

	@BeforeEach
	void setUp() {
		// 도서를 관심등록한 유저
		User user = User.builder()
			.email("kwondh5217@gmail.com")
			.nickname("nickname")
			.password("password")
			.areaCode("areaCode")
			.build();
		User wishBookUser = this.userRepository.save(user);

		// 대여 신청자
		user = User.builder()
			.email("trewq231@naver.com")
			.nickname("nickname")
			.password("password")
			.areaCode("areaCode")
			.build();
		this.user = this.userRepository.save(user);

		Point point = Point.builder()
			.user(this.user)
			.history(PointHistory.BOOK_RENTAL)
			.amount(PointHistory.BOOK_RENTAL.getAmount())
			.build();
		this.pointRepository.save(point);

		// 도서 소유자
		User owner = User.builder()
			.email("test@test.com")
			.nickname("nickname")
			.password("password")
			.areaCode("areaCode")
			.build();
		this.owner = this.userRepository.save(owner);

		// 사용자 도서
		Userbook build = Userbook.builder()
			.registerType(RegisterType.RENTAL_EXCHANGE)
			.tradeStatus(TradeStatus.RENTAL_EXCHANGE_AVAILABLE)
			.user(owner)
			.build();
		this.userbook = this.userbookRepository.save(build);
		this.userbook = this.userbookRepository.save(userbook);
	}

	@DisplayName("도서의 ID에 대해 대여를 신청한다")
	@Test
	void rentalOffer() {
		// given
		Long userbooksId = userbook.getId();
		given(this.userHelper.findCurrentUser()).willReturn(user);

		// when
		RentalOfferResponse rentalOfferResponse = this.rentalService.rentalOffer(userbooksId);

		// then
		assertNotNull(rentalOfferResponse);

		Optional<Rental> byId = this.rentalRepository.findById(rentalOfferResponse.rentalId());
		assertTrue(byId.isPresent());

		Rental createdRental = byId.get();
		assertEquals(createdRental.getUserbook().getId(), userbooksId);
		assertEquals(createdRental.getUser().getId(), user.getId());
	}

	@DisplayName("존재하지 않는 도서에 대해서는 대여 신청을 할 수 없다")
	@Test
	void rentalOffer_doseNotExist_fail() {
		// given
		Long invalidUserbookId = 241234312L;
		String expectedMessage = "존재하지 않는 도서입니다.";

		// expected
		assertThrows(ErrorException.class,
			() -> this.rentalService.rentalOffer(invalidUserbookId));
	}

	@DisplayName("대여 불가능한 도서에 대해서는 대여 신청을 할 수 없다")
	@Test
	void rentalOffer_tradeStatusUnavailable_fail() {
		// given
		userbook.inactivate();
		userbook = this.userbookRepository.save(userbook);
		given(this.userHelper.findCurrentUser()).willReturn(user);

		// expected
		assertThrows(IllegalStateException.class,
			() -> this.rentalService.rentalOffer(userbook.getId()));
	}

	@DisplayName("회원이 대여신청을 수락한다. 기존에 있던 대여신청은 자동 거절이 된다")
	@Test
	void offerRespond_approve() {
		// given
		Rental save = this.rentalRepository.save(createRental(user, userbook, OFFERING)); // 수락할 대여
		User anotherUser = this.userRepository.save(createUser("anotherUser"));
		Rental anotherRental = this.rentalRepository.save(createRental(anotherUser, userbook, OFFERING));

		RentalOfferRespondRequest request = new RentalOfferRespondRequest(true);
		doNothing().when(mailSender).send(any(MimeMessage.class));
		given(this.userHelper.findCurrentUser()).willReturn(owner);

		// when
		this.rentalService.offerRespond(save.getId(), request);

		// then
		Optional<Rental> byId = this.rentalRepository.findById(save.getId());
		assertTrue(byId.isPresent());

		Rental findById = byId.get();
		assertNotNull(findById.getStartDate());

		userbook = findById.getUserbook();
		assertEquals(userbook.getTradeStatus(), TradeStatus.RENTED);

		Rental rental = this.rentalRepository.findById(anotherRental.getId()).get();
		assertEquals(rental.getRentalStatus(), REJECTED);
	}

	@DisplayName("회원이 발생한 대여신청을 삭제한다")
	@Test
	void deleteRentalOffer() {
		// given
		Rental rental = Rental.builder()
			.user(user)
			.userbook(userbook)
			.build();
		Rental save = this.rentalRepository.save(rental);
		given(this.userHelper.findCurrentUser()).willReturn(user);

		// when
		this.rentalService.deleteRentalOffer(save.getId());

		// then
		Optional<Rental> byId = this.rentalRepository.findById(save.getId());
		assertFalse(byId.isPresent());
	}

	@DisplayName("도서 소유자가 반납완료를 요청한다")
	@Test
	void giveBack() {
		// given
		Rental rental = Rental.builder()
			.user(user)
			.userbook(userbook)
			.rentalStatus(RentalStatus.IN_PROGRESS)
			.build();
		Rental save = this.rentalRepository.save(rental);

		Extension extension = this.extensionRepository.save(
			Extension.builder()
				.rental(save)
				.createdAt(LocalDateTime.of(2024, 05, 01, 12, 00, 00))
				.extensionStatus(ExtensionStatus.APPROVED)
				.build()
		);

		given(this.userHelper.findCurrentUser()).willReturn(owner);

		// when
		this.rentalService.giveBack(save.getId());

		// then
		Optional<Rental> byId = this.rentalRepository.findById(save.getId());
		assertTrue(byId.isPresent());
		Optional<Extension> extensionOptional = this.extensionRepository.findById(extension.getId());
		assertFalse(extensionOptional.isPresent());


		rental = byId.get();
		assertNotNull(rental.getEndDate());
		assertNotEquals(rental.getUserbook().getTradeStatus(), TradeStatus.UNAVAILABLE);
	}

	@DisplayName("도서 소유자가 도서 ID로 반납완료를 요청한다")
	@Test
	void giveBackByUserbookId() {
		// given
		Rental rental = Rental.builder()
			.user(user)
			.userbook(userbook)
			.rentalStatus(RentalStatus.IN_PROGRESS)
			.build();
		Rental save = this.rentalRepository.save(rental);
		given(this.userHelper.findCurrentUser()).willReturn(owner);

		// when
		this.rentalService.giveBackByUserbookId(userbook.getId());

		// then
		Optional<Rental> byId = this.rentalRepository.findById(save.getId());
		assertTrue(byId.isPresent());

		rental = byId.get();
		assertNotNull(rental.getEndDate());
		assertNotEquals(rental.getUserbook().getTradeStatus(), TradeStatus.UNAVAILABLE);
	}

	@DisplayName("회원이 대여한 도서에 대해서 연장 신청을 한다")
	@Test
	void extension() {
		// given
		Rental rental = userbook.createRental(user);
		rental.respond(true);
		Rental save = this.rentalRepository.save(rental);
		given(this.userHelper.findCurrentUser()).willReturn(user);

		// when
		Long extensionId = this.extensionService.extensionRental(save.getId());

		// then
		Optional<Extension> byId = this.extensionRepository.findById(extensionId);
		assertTrue(byId.isPresent());
	}

	@DisplayName("조건에 맞는 대여 목록을 조회한다.")
	@Test
	void findRentalByCondition() {
		// given
		User me = createUser("me");
		User user = createUser("someone");
		userRepository.saveAll(List.of(me, user));

		Userbook mine = createUserbook(me, "001");
		Userbook userbook = createUserbook(user, "002");
		userbookRepository.saveAll(List.of(mine, userbook));

		Rental asSender = createRental(me, userbook, OFFERING);
		Rental asReceiver = createRental(user, mine, OFFERING);
		Rental rental1 = createRental(me, userbook, COMPLETED);
		Rental rental2 = createRental(me, userbook, REJECTED);
		Rental rental3 = createRental(me, userbook, IN_PROGRESS);
		rentalRepository.saveAll(List.of(asSender, asReceiver, rental1, rental2, rental3));

		given(userHelper.findCurrentUser()).willReturn(me);
		RentalFindCondition condition = new RentalFindCondition(RECEIVER, OFFERING);

		// when
		Page<RentalResponse> result = rentalService.findByCondition(condition, PageRequest.of(0, 10));

		///then
		List<RentalResponse> rentals = result.getContent();
		assertRentalWithRentalStatus(rentals, 1, OFFERING);

		UserbookResponse receiverBook = rentals.get(0).userbook();
		assertThatUserbookMatchExactly(receiverBook, mine);
	}

	@DisplayName("포인트가 없다면 도서 대여 신청을 할 수 없다")
	@Test
	void rejectRentalOffer() {
		// given
		User rentalUser = this.userRepository.save(createUser("test"));
		Userbook savedUserbook = this.userbookRepository.save(createUserbook(user, "test"));
		given(this.userHelper.findCurrentUser()).willReturn(rentalUser);

		// expected
		ErrorException errorException = assertThrows(ErrorException.class,
			() -> this.rentalService.rentalOffer(savedUserbook.getId()));
		assertEquals(errorException.getErrorCode().getMessage(), ErrorCode.NotEnoughPoint.getMessage());
	}

	@DisplayName("대여는 중복으로 신청할 수 없다")
	@Test
	void rejectRentalOfferDuplicated() {
		// given
		Rental rental = this.rentalRepository.save(createRental(user, userbook, OFFERING));
		given(this.userHelper.findCurrentUser()).willReturn(user);

		// expected
		ErrorException errorException = assertThrows(ErrorException.class,
			() -> this.rentalService.rentalOffer(userbook.getId()));
		assertEquals(errorException.getErrorCode().getMessage(), ErrorCode.NotAcceptDuplicate.getMessage());
	}

	private User createUser(String nickname) {
		return User.builder()
			.email("user@email.com")
			.password("encrypted password")
			.nickname(nickname)
			.areaCode("1234567")
			.build();
	}

	private Book createBook(String isbn, String title, LocalDate publicationDate) {
		return Book.builder()
			.isbn(isbn)
			.title(title)
			.author("author")
			.publisher("publisher")
			.publicationDate(publicationDate)
			.thumbnail("thumbnail")
			.description("description")
			.build();
	}

	private Userbook createUserbook(User user, String isbn) {
		LocalDate publicationDate = LocalDate.of(2024, 7, 22);
		Book book = createBook(isbn, "title", publicationDate);
		bookRepository.save(book);

		return Userbook.builder()
			.book(book)
			.user(user)
			.qualityStatus(NORMAL)
			.registerType(RENTAL)
			.tradeStatus(RENTAL_AVAILABLE)
			.build();
	}

	private Rental createRental(User user, Userbook userbook, RentalStatus rentalStatus) {
		return Rental.builder()
			.user(user)
			.userbook(userbook)
			.rentalStatus(rentalStatus)
			.build();
	}

	private void assertRentalWithRentalStatus(List<RentalResponse> resultContent, int size, RentalStatus rentalStatus) {
		assertThat(resultContent)
			.hasSize(size)
			.extracting("rentalStatus").contains(rentalStatus);
	}

	private void assertThatUserbookMatchExactly(UserbookResponse target, Userbook pair) {
		assertThat(target)
			.extracting("id", "qualityStatus", "registerType", "tradeStatus")
			.containsExactlyInAnyOrder(pair.getId(), pair.getQualityStatus(), pair.getRegisterType(),
				pair.getTradeStatus());
	}
}