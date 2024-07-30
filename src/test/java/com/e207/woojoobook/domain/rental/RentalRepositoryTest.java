package com.e207.woojoobook.domain.rental;

import static com.e207.woojoobook.domain.rental.RentalStatus.*;
import static com.e207.woojoobook.domain.rental.RentalUserCondition.*;
import static com.e207.woojoobook.domain.userbook.QualityStatus.*;
import static com.e207.woojoobook.domain.userbook.RegisterType.*;
import static com.e207.woojoobook.domain.userbook.TradeStatus.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.e207.woojoobook.domain.book.Book;
import com.e207.woojoobook.domain.book.BookRepository;
import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.user.UserRepository;
import com.e207.woojoobook.domain.userbook.Userbook;
import com.e207.woojoobook.domain.userbook.UserbookRepository;
import com.e207.woojoobook.global.util.DynamicQueryHelper;

@Import(DynamicQueryHelper.class)
@DataJpaTest
class RentalRepositoryTest {

	@Autowired
	RentalRepository rentalRepository;
	@Autowired
	private UserbookRepository userbookRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BookRepository bookRepository;

	@DisplayName("대여 신청한 목록을 조회한다.")
	@Test
	void findOfferingRentalAsSenderSuccess() {
		// given
		User me = createUser("me");
		User user = createUser("someone");
		userRepository.saveAll(List.of(me, user));

		Userbook mine = createUserbook(me, "001");
		Userbook userbook = createUserbook(me, "002");
		userbookRepository.saveAll(List.of(mine, userbook));

		Rental asSender = createRental(me, userbook, OFFERING);
		Rental asReceiver = createRental(user, mine, OFFERING);
		Rental rental1 = createRental(user, mine, REJECTED);
		Rental rental2 = createRental(user, mine, IN_PROGRESS);
		Rental rental3 = createRental(user, mine, COMPLETED);
		rentalRepository.saveAll(List.of(asSender, asReceiver, rental1, rental2, rental3));

		// when
		Page<Rental> result = rentalRepository.findByStatusAndUserCondition(me.getId(), OFFERING, SENDER,
			PageRequest.of(0, 10));

		///then
		List<Rental> rentals = result.getContent();
		assertRentalWithRentalStatus(rentals, 1, OFFERING);

		Userbook receiverBook = rentals.get(0).getUserbook();
		assertThatUserbookMatchExactly(receiverBook, userbook);
	}

	@DisplayName("대여 신청을 받은 목록을 조회한다.")
	@Test
	void findOfferingRentalAsReceiverSuccess() {
		// given
		User me = createUser("me");
		User user = createUser("someone");
		userRepository.saveAll(List.of(me, user));

		Userbook mine = createUserbook(me, "001");
		Userbook userbook = createUserbook(user, "002");
		userbookRepository.saveAll(List.of(mine, userbook));

		Rental asSender = createRental(me, userbook, OFFERING);
		Rental asReceiver = createRental(user, mine, OFFERING);
		Rental rental1 = createRental(me, userbook, REJECTED);
		Rental rental2 = createRental(me, userbook, IN_PROGRESS);
		Rental rental3 = createRental(me, userbook, COMPLETED);
		rentalRepository.saveAll(List.of(asSender, asReceiver, rental1, rental2, rental3));

		// when
		Page<Rental> result = rentalRepository.findByStatusAndUserCondition(me.getId(), OFFERING, RECEIVER,
			PageRequest.of(0, 10));

		List<Rental> exchanges = result.getContent();
		assertRentalWithRentalStatus(exchanges, 1, OFFERING);

		Userbook receiverBook = exchanges.get(0).getUserbook();
		assertThatUserbookMatchExactly(receiverBook, mine);
	}

	@DisplayName("모든 대여 신청 목록을 조회한다.")
	@Test
	void findOfferingRentalAsAllSuccess() {
		// given
		User me = createUser("me");
		User user = createUser("someone");
		userRepository.saveAll(List.of(me, user));

		Userbook mine = createUserbook(me, "001");
		Userbook userbook = createUserbook(user, "002");
		userbookRepository.saveAll(List.of(mine, userbook));

		Rental asSender = createRental(me, userbook, OFFERING);
		Rental asReceiver = createRental(user, mine, OFFERING);
		Rental rental1 = createRental(me, userbook, REJECTED);
		Rental rental2 = createRental(me, userbook, IN_PROGRESS);
		Rental rental3 = createRental(me, userbook, COMPLETED);
		rentalRepository.saveAll(List.of(asSender, asReceiver, rental1, rental2, rental3));

		// when
		Page<Rental> result = rentalRepository.findByStatusAndUserCondition(me.getId(), OFFERING, SENDER_RECEIVER,
			PageRequest.of(0, 10));

		///then
		List<Rental> exchanges = result.getContent();
		assertRentalWithRentalStatus(exchanges, 2, OFFERING);
	}

	@DisplayName("거절당한 대여 신청 목록을 조회한다.")
	@Test
	void findRejectRentalAsSenderSuccess() {
		// given
		User me = createUser("me");
		User user = createUser("someone");
		userRepository.saveAll(List.of(me, user));

		Userbook mine = createUserbook(me, "001");
		Userbook userbook = createUserbook(user, "002");
		userbookRepository.saveAll(List.of(mine, userbook));

		Rental asSender = createRental(me, userbook, REJECTED);
		Rental asReceiver = createRental(user, mine, REJECTED);
		Rental rental1 = createRental(user, mine, OFFERING);
		Rental rental2 = createRental(user, mine, IN_PROGRESS);
		Rental rental3 = createRental(user, mine, COMPLETED);
		rentalRepository.saveAll(List.of(asSender, asReceiver, rental1, rental2, rental3));

		// when
		Page<Rental> result = rentalRepository.findByStatusAndUserCondition(me.getId(), REJECTED, SENDER,
			PageRequest.of(0, 10));

		///then
		List<Rental> exchanges = result.getContent();
		assertRentalWithRentalStatus(exchanges, 1, REJECTED);

		Userbook receiverBook = exchanges.get(0).getUserbook();
		assertThatUserbookMatchExactly(receiverBook, userbook);
	}

	@DisplayName("거절한 대여 신청 목록을 조회한다.")
	@Test
	void findRejectRentalAsReceiverSuccess() {
		// given
		User me = createUser("me");
		User user = createUser("someone");
		userRepository.saveAll(List.of(me, user));

		Userbook mine = createUserbook(me, "001");
		Userbook userbook = createUserbook(user, "002");
		userbookRepository.saveAll(List.of(mine, userbook));

		Rental asSender = createRental(me, userbook, REJECTED);
		Rental asReceiver = createRental(user, mine, REJECTED);
		Rental rental1 = createRental(me, userbook, OFFERING);
		Rental rental2 = createRental(me, userbook, IN_PROGRESS);
		Rental rental3 = createRental(me, userbook, COMPLETED);
		rentalRepository.saveAll(List.of(asSender, asReceiver, rental1, rental2, rental3));

		// when
		Page<Rental> result = rentalRepository.findByStatusAndUserCondition(me.getId(), REJECTED, RECEIVER,
			PageRequest.of(0, 10));

		///then
		List<Rental> exchanges = result.getContent();
		assertRentalWithRentalStatus(exchanges, 1, REJECTED);

		Userbook receiverBook = exchanges.get(0).getUserbook();
		assertThatUserbookMatchExactly(receiverBook, mine);
	}

	@DisplayName("거절당한 또는 거절한 대여 신청 목록을 조회한다.")
	@Test
	void findRejectRentalAsAllSuccess() {
		// given
		User me = createUser("me");
		User user = createUser("someone");
		userRepository.saveAll(List.of(me, user));

		Userbook mine = createUserbook(me, "001");
		Userbook userbook = createUserbook(user, "002");
		userbookRepository.saveAll(List.of(mine, userbook));

		Rental asSender = createRental(me, userbook, REJECTED);
		Rental asReceiver = createRental(user, mine, REJECTED);
		Rental rental1 = createRental(me, userbook, OFFERING);
		Rental rental2 = createRental(me, userbook, IN_PROGRESS);
		Rental rental3 = createRental(me, userbook, COMPLETED);
		rentalRepository.saveAll(List.of(asSender, asReceiver, rental1, rental2, rental3));

		// when
		Page<Rental> result = rentalRepository.findByStatusAndUserCondition(me.getId(), REJECTED, SENDER_RECEIVER,
			PageRequest.of(0, 10));

		///then
		List<Rental> exchanges = result.getContent();
		assertRentalWithRentalStatus(exchanges, 2, REJECTED);
	}

	@DisplayName("대여 중인 목록을 조회한다.")
	@Test
	void findInProgressRentalAsSenderSuccess() {
		// given
		User me = createUser("me");
		User user = createUser("someone");
		userRepository.saveAll(List.of(me, user));

		Userbook mine = createUserbook(me, "001");
		Userbook userbook = createUserbook(user, "002");
		userbookRepository.saveAll(List.of(mine, userbook));

		Rental asSender = createRental(me, userbook, IN_PROGRESS);
		Rental asReceiver = createRental(user, mine, IN_PROGRESS);
		Rental rental1 = createRental(user, mine, OFFERING);
		Rental rental2 = createRental(user, mine, REJECTED);
		Rental rental3 = createRental(user, mine, COMPLETED);
		rentalRepository.saveAll(List.of(asSender, asReceiver, rental1, rental2, rental3));

		// when
		Page<Rental> result = rentalRepository.findByStatusAndUserCondition(me.getId(), IN_PROGRESS, SENDER,
			PageRequest.of(0, 10));

		///then
		List<Rental> exchanges = result.getContent();
		assertRentalWithRentalStatus(exchanges, 1, IN_PROGRESS);

		Userbook receiverBook = exchanges.get(0).getUserbook();
		assertThatUserbookMatchExactly(receiverBook, userbook);
	}

	@DisplayName("대여해준 목록을 조회한다.")
	@Test
	void findInProgressRentalAsReceiverSuccess() {
		// given
		User me = createUser("me");
		User user = createUser("someone");
		userRepository.saveAll(List.of(me, user));

		Userbook mine = createUserbook(me, "001");
		Userbook userbook = createUserbook(user, "002");
		userbookRepository.saveAll(List.of(mine, userbook));

		Rental asSender = createRental(me, userbook, IN_PROGRESS);
		Rental asReceiver = createRental(user, mine, IN_PROGRESS);
		Rental rental1 = createRental(me, userbook, OFFERING);
		Rental rental2 = createRental(me, userbook, REJECTED);
		Rental rental3 = createRental(me, userbook, COMPLETED);
		rentalRepository.saveAll(List.of(asSender, asReceiver, rental1, rental2, rental3));

		// when
		Page<Rental> result = rentalRepository.findByStatusAndUserCondition(me.getId(), IN_PROGRESS, RECEIVER,
			PageRequest.of(0, 10));

		///then
		List<Rental> exchanges = result.getContent();
		assertRentalWithRentalStatus(exchanges, 1, IN_PROGRESS);

		Userbook receiverBook = exchanges.get(0).getUserbook();
		assertThatUserbookMatchExactly(receiverBook, mine);
	}

	@DisplayName("대여중 또는 대여해준 목록을 조회한다.")
	@Test
	void findInProgressRentalAsAllSuccess() {
		// given
		User me = createUser("me");
		User user = createUser("someone");
		userRepository.saveAll(List.of(me, user));

		Userbook mine = createUserbook(me, "001");
		Userbook userbook = createUserbook(user, "002");
		userbookRepository.saveAll(List.of(mine, userbook));

		Rental asSender = createRental(me, userbook, IN_PROGRESS);
		Rental asReceiver = createRental(user, mine, IN_PROGRESS);
		Rental rental1 = createRental(me, userbook, OFFERING);
		Rental rental2 = createRental(me, userbook, REJECTED);
		Rental rental3 = createRental(me, userbook, COMPLETED);
		rentalRepository.saveAll(List.of(asSender, asReceiver, rental1, rental2, rental3));

		// when
		Page<Rental> result = rentalRepository.findByStatusAndUserCondition(me.getId(), IN_PROGRESS, SENDER_RECEIVER,
			PageRequest.of(0, 10));

		///then
		List<Rental> exchanges = result.getContent();
		assertRentalWithRentalStatus(exchanges, 2, IN_PROGRESS);
	}

	@DisplayName("대여했던 목록을 조회한다.")
	@Test
	void findCompletedRentalAsSenderSuccess() {
		// given
		User me = createUser("me");
		User user = createUser("someone");
		userRepository.saveAll(List.of(me, user));

		Userbook mine = createUserbook(me, "001");
		Userbook userbook = createUserbook(user, "002");
		userbookRepository.saveAll(List.of(mine, userbook));

		Rental asSender = createRental(me, userbook, COMPLETED);
		Rental asReceiver = createRental(user, mine, COMPLETED);
		Rental rental1 = createRental(user, mine, OFFERING);
		Rental rental2 = createRental(user, mine, REJECTED);
		Rental rental3 = createRental(user, mine, IN_PROGRESS);
		rentalRepository.saveAll(List.of(asSender, asReceiver, rental1, rental2, rental3));

		// when
		Page<Rental> result = rentalRepository.findByStatusAndUserCondition(me.getId(), COMPLETED, SENDER,
			PageRequest.of(0, 10));

		///then
		List<Rental> exchanges = result.getContent();
		assertRentalWithRentalStatus(exchanges, 1, COMPLETED);

		Userbook receiverBook = exchanges.get(0).getUserbook();
		assertThatUserbookMatchExactly(receiverBook, userbook);
	}

	@DisplayName("대여해줬던 목록을 조회한다.")
	@Test
	void findCompletedRentalAsReceiverSuccess() {
		// given
		User me = createUser("me");
		User user = createUser("someone");
		userRepository.saveAll(List.of(me, user));

		Userbook mine = createUserbook(me, "001");
		Userbook userbook = createUserbook(user, "002");
		userbookRepository.saveAll(List.of(mine, userbook));

		Rental asSender = createRental(me, userbook, COMPLETED);
		Rental asReceiver = createRental(user, mine, COMPLETED);
		Rental rental1 = createRental(me, userbook, OFFERING);
		Rental rental2 = createRental(me, userbook, REJECTED);
		Rental rental3 = createRental(me, userbook, IN_PROGRESS);
		rentalRepository.saveAll(List.of(asSender, asReceiver, rental1, rental2, rental3));

		// when
		Page<Rental> result = rentalRepository.findByStatusAndUserCondition(me.getId(), COMPLETED, RECEIVER,
			PageRequest.of(0, 10));

		///then
		List<Rental> exchanges = result.getContent();
		assertRentalWithRentalStatus(exchanges, 1, COMPLETED);

		Userbook receiverBook = exchanges.get(0).getUserbook();
		assertThatUserbookMatchExactly(receiverBook, mine);
	}

	@DisplayName("대여했던 또는 대여해줬던 목록을 조회한다.")
	@Test
	void findCompletedRentalAsAllSuccess() {
		// given
		User me = createUser("me");
		User user = createUser("someone");
		userRepository.saveAll(List.of(me, user));

		Userbook mine = createUserbook(me, "001");
		Userbook userbook = createUserbook(user, "002");
		userbookRepository.saveAll(List.of(mine, userbook));

		Rental asSender = createRental(me, userbook, COMPLETED);
		Rental asReceiver = createRental(user, mine, COMPLETED);
		Rental rental1 = createRental(me, userbook, OFFERING);
		Rental rental2 = createRental(me, userbook, REJECTED);
		Rental rental3 = createRental(me, userbook, IN_PROGRESS);
		rentalRepository.saveAll(List.of(asSender, asReceiver, rental1, rental2, rental3));

		// when
		Page<Rental> result = rentalRepository.findByStatusAndUserCondition(me.getId(), COMPLETED, SENDER_RECEIVER,
			PageRequest.of(0, 10));

		///then
		List<Rental> exchanges = result.getContent();
		assertRentalWithRentalStatus(exchanges, 2, COMPLETED);
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

	private void assertRentalWithRentalStatus(List<Rental> resultContent, int size, RentalStatus rentalStatus) {
		assertThat(resultContent)
			.hasSize(size)
			.extracting("rentalStatus").contains(rentalStatus);
	}

	private void assertThatUserbookMatchExactly(Userbook target, Userbook pair) {
		assertThat(target)
			.extracting("id", "qualityStatus", "registerType", "tradeStatus")
			.containsExactlyInAnyOrder(pair.getId(), pair.getQualityStatus(), pair.getRegisterType(),
				pair.getTradeStatus());
	}
}