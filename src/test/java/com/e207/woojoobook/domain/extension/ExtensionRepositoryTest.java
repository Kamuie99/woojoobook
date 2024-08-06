package com.e207.woojoobook.domain.extension;

import static com.e207.woojoobook.domain.exchange.TradeUserCondition.*;
import static com.e207.woojoobook.domain.userbook.QualityStatus.*;
import static com.e207.woojoobook.domain.userbook.RegisterType.*;
import static com.e207.woojoobook.domain.userbook.TradeStatus.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.e207.woojoobook.domain.book.Book;
import com.e207.woojoobook.domain.book.BookRepository;
import com.e207.woojoobook.domain.rental.Rental;
import com.e207.woojoobook.domain.rental.RentalRepository;
import com.e207.woojoobook.domain.rental.RentalStatus;
import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.user.UserRepository;
import com.e207.woojoobook.domain.userbook.Userbook;
import com.e207.woojoobook.domain.userbook.UserbookRepository;

@DataJpaTest
class ExtensionRepositoryTest {

	@Autowired
	private ExtensionRepository extensionRepository;
	@Autowired
	private RentalRepository rentalRepository;
	@Autowired
	private UserbookRepository userbookRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BookRepository bookRepository;

	@DisplayName("연장 신청한 목록을 조회한다.")
	@Test
	void findOfferingExtensionAsSenderSuccess() {
		// given
		User me = createUser("me");
		User user = createUser("someone");
		userRepository.saveAll(List.of(me, user));

		Userbook mine = createUserbook(me, "001");
		Userbook userbook = createUserbook(me, "002");
		userbookRepository.saveAll(List.of(mine, userbook));

		Rental rentalAsSender = createRental(me, userbook, RentalStatus.OFFERING);
		Rental rentalAsReceiver = createRental(user, mine, RentalStatus.OFFERING);
		rentalRepository.saveAll(List.of(rentalAsSender, rentalAsReceiver));

		Extension extensionAsSender = createExtension(rentalAsSender, ExtensionStatus.OFFERING);
		Extension extensionAsReceiver = createExtension(rentalAsReceiver, ExtensionStatus.OFFERING);
		extensionRepository.saveAll(List.of(extensionAsSender, extensionAsReceiver));

		// when
		Page<Extension> result = extensionRepository.findByStatusAndUserCondition(me.getId(), ExtensionStatus.OFFERING,
			SENDER, PageRequest.of(0, 10));

		///then
		List<Extension> extensions = result.getContent();
		assertExtensionWithExtensionStatus(extensions, 1, ExtensionStatus.OFFERING);

		Userbook targetbook = extensions.get(0).getRental().getUserbook();
		assertThatUserbookMatchExactly(targetbook, userbook);
	}

	@DisplayName("연장 신청을 받은 목록을 조회한다.")
	@Test
	void findOfferingExtensionAsReceiverSuccess() {
		// given
		User me = createUser("me");
		User user = createUser("someone");
		userRepository.saveAll(List.of(me, user));

		Userbook mine = createUserbook(me, "001");
		Userbook userbook = createUserbook(user, "002");
		userbookRepository.saveAll(List.of(mine, userbook));

		Rental rentalAsSender = createRental(me, userbook, RentalStatus.OFFERING);
		Rental rentalAsReceiver = createRental(user, mine, RentalStatus.OFFERING);
		rentalRepository.saveAll(List.of(rentalAsSender, rentalAsReceiver));

		Extension extensionAsSender = createExtension(rentalAsSender, ExtensionStatus.OFFERING);
		Extension extensionAsReceiver = createExtension(rentalAsReceiver, ExtensionStatus.OFFERING);
		extensionRepository.saveAll(List.of(extensionAsSender, extensionAsReceiver));

		// when
		Page<Extension> result = extensionRepository.findByStatusAndUserCondition(me.getId(), ExtensionStatus.OFFERING,
			RECEIVER, PageRequest.of(0, 10));

		//then
		List<Extension> extensions = result.getContent();
		assertExtensionWithExtensionStatus(extensions, 1, ExtensionStatus.OFFERING);

		Userbook targetbook = extensions.get(0).getRental().getUserbook();
		assertThatUserbookMatchExactly(targetbook, mine);
	}

	@DisplayName("모든 연장 신청 목록을 조회한다.")
	@Test
	void findOfferingExtensionAsAllSuccess() {
		// given
		User me = createUser("me");
		User user = createUser("someone");
		userRepository.saveAll(List.of(me, user));

		Userbook mine = createUserbook(me, "001");
		Userbook userbook = createUserbook(user, "002");
		userbookRepository.saveAll(List.of(mine, userbook));

		Rental rentalAsSender = createRental(me, userbook, RentalStatus.OFFERING);
		Rental rentalAsReceiver = createRental(user, mine, RentalStatus.OFFERING);
		rentalRepository.saveAll(List.of(rentalAsSender, rentalAsReceiver));

		Extension extensionAsSender = createExtension(rentalAsSender, ExtensionStatus.OFFERING);
		Extension extensionAsReceiver = createExtension(rentalAsReceiver, ExtensionStatus.OFFERING);
		extensionRepository.saveAll(List.of(extensionAsSender, extensionAsReceiver));

		// when
		Page<Extension> result = extensionRepository.findByStatusAndUserCondition(me.getId(), ExtensionStatus.OFFERING,
			SENDER_RECEIVER, PageRequest.of(0, 10));

		///then
		List<Extension> extensions = result.getContent();
		assertExtensionWithExtensionStatus(extensions, 2, ExtensionStatus.OFFERING);
	}

	@DisplayName("거절당한 연장 신청 목록을 조회한다.")
	@Test
	void findRejectExtensionAsSenderSuccess() {
		// given
		User me = createUser("me");
		User user = createUser("someone");
		userRepository.saveAll(List.of(me, user));

		Userbook mine = createUserbook(me, "001");
		Userbook userbook = createUserbook(user, "002");
		userbookRepository.saveAll(List.of(mine, userbook));

		Rental rentalAsSender = createRental(me, userbook, RentalStatus.OFFERING);
		Rental rentalAsReceiver = createRental(user, mine, RentalStatus.OFFERING);
		rentalRepository.saveAll(List.of(rentalAsSender, rentalAsReceiver));

		Extension extensionAsSender = createExtension(rentalAsSender, ExtensionStatus.REJECTED);
		Extension extensionAsReceiver = createExtension(rentalAsReceiver, ExtensionStatus.REJECTED);
		extensionRepository.saveAll(List.of(extensionAsSender, extensionAsReceiver));

		// when
		Page<Extension> result = extensionRepository.findByStatusAndUserCondition(me.getId(), ExtensionStatus.REJECTED,
			SENDER, PageRequest.of(0, 10));

		///then
		List<Extension> extensions = result.getContent();
		assertExtensionWithExtensionStatus(extensions, 1, ExtensionStatus.REJECTED);

		Userbook targetbook = extensions.get(0).getRental().getUserbook();
		assertThatUserbookMatchExactly(targetbook, userbook);
	}

	@DisplayName("거절한 연장 신청 목록을 조회한다.")
	@Test
	void findRejectExtensionAsReceiverSuccess() {
		// given
		User me = createUser("me");
		User user = createUser("someone");
		userRepository.saveAll(List.of(me, user));

		Userbook mine = createUserbook(me, "001");
		Userbook userbook = createUserbook(user, "002");
		userbookRepository.saveAll(List.of(mine, userbook));

		Rental rentalAsSender = createRental(me, userbook, RentalStatus.OFFERING);
		Rental rentalAsReceiver = createRental(user, mine, RentalStatus.OFFERING);
		rentalRepository.saveAll(List.of(rentalAsSender, rentalAsReceiver));

		Extension extensionAsSender = createExtension(rentalAsSender, ExtensionStatus.REJECTED);
		Extension extensionAsReceiver = createExtension(rentalAsReceiver, ExtensionStatus.REJECTED);
		extensionRepository.saveAll(List.of(extensionAsSender, extensionAsReceiver));

		// when
		Page<Extension> result = extensionRepository.findByStatusAndUserCondition(me.getId(), ExtensionStatus.REJECTED,
			RECEIVER, PageRequest.of(0, 10));

		///then
		List<Extension> exchanges = result.getContent();
		assertExtensionWithExtensionStatus(exchanges, 1, ExtensionStatus.REJECTED);

		Userbook targetbook = exchanges.get(0).getRental().getUserbook();
		assertThatUserbookMatchExactly(targetbook, mine);
	}

	@DisplayName("거절처리된 모든 연장 목록을 조회한다.")
	@Test
	void findRejectExtensionAsAllSuccess() {
		// given
		User me = createUser("me");
		User user = createUser("someone");
		userRepository.saveAll(List.of(me, user));

		Userbook mine = createUserbook(me, "001");
		Userbook userbook = createUserbook(user, "002");
		userbookRepository.saveAll(List.of(mine, userbook));

		Rental rentalAsSender = createRental(me, userbook, RentalStatus.OFFERING);
		Rental rentalAsReceiver = createRental(user, mine, RentalStatus.OFFERING);
		rentalRepository.saveAll(List.of(rentalAsSender, rentalAsReceiver));

		Extension extensionAsSender = createExtension(rentalAsSender, ExtensionStatus.REJECTED);
		Extension extensionAsReceiver = createExtension(rentalAsReceiver, ExtensionStatus.REJECTED);
		extensionRepository.saveAll(List.of(extensionAsSender, extensionAsReceiver));

		// when
		Page<Extension> result = extensionRepository.findByStatusAndUserCondition(me.getId(), ExtensionStatus.REJECTED,
			SENDER_RECEIVER, PageRequest.of(0, 10));

		///then
		List<Extension> exchanges = result.getContent();
		assertExtensionWithExtensionStatus(exchanges, 2, ExtensionStatus.REJECTED);
	}

	@DisplayName("연장했던 목록을 조회한다.")
	@Test
	void findCompletedExtensionAsSenderSuccess() {
		// given
		User me = createUser("me");
		User user = createUser("someone");
		userRepository.saveAll(List.of(me, user));

		Userbook mine = createUserbook(me, "001");
		Userbook userbook = createUserbook(user, "002");
		userbookRepository.saveAll(List.of(mine, userbook));

		Rental rentalAsSender = createRental(me, userbook, RentalStatus.OFFERING);
		Rental rentalAsReceiver = createRental(user, mine, RentalStatus.OFFERING);
		rentalRepository.saveAll(List.of(rentalAsSender, rentalAsReceiver));

		Extension extensionAsSender = createExtension(rentalAsSender, ExtensionStatus.APPROVED);
		Extension extensionAsReceiver = createExtension(rentalAsReceiver, ExtensionStatus.APPROVED);
		extensionRepository.saveAll(List.of(extensionAsSender, extensionAsReceiver));

		// when
		Page<Extension> result = extensionRepository.findByStatusAndUserCondition(me.getId(), ExtensionStatus.APPROVED,
			SENDER, PageRequest.of(0, 10));

		///then
		List<Extension> exchanges = result.getContent();
		assertExtensionWithExtensionStatus(exchanges, 1, ExtensionStatus.APPROVED);

		Userbook targetbook = exchanges.get(0).getRental().getUserbook();
		assertThatUserbookMatchExactly(targetbook, userbook);
	}

	@DisplayName("연장을 허가했던 목록을 조회한다.")
	@Test
	void findCompletedExtensionAsReceiverSuccess() {
		// given
		User me = createUser("me");
		User user = createUser("someone");
		userRepository.saveAll(List.of(me, user));

		Userbook mine = createUserbook(me, "001");
		Userbook userbook = createUserbook(user, "002");
		userbookRepository.saveAll(List.of(mine, userbook));

		Rental rentalAsSender = createRental(me, userbook, RentalStatus.OFFERING);
		Rental rentalAsReceiver = createRental(user, mine, RentalStatus.OFFERING);
		rentalRepository.saveAll(List.of(rentalAsSender, rentalAsReceiver));

		Extension extensionAsSender = createExtension(rentalAsSender, ExtensionStatus.APPROVED);
		Extension extensionAsReceiver = createExtension(rentalAsReceiver, ExtensionStatus.APPROVED);
		extensionRepository.saveAll(List.of(extensionAsSender, extensionAsReceiver));

		// when
		Page<Extension> result = extensionRepository.findByStatusAndUserCondition(me.getId(), ExtensionStatus.APPROVED,
			RECEIVER, PageRequest.of(0, 10));

		///then
		List<Extension> exchanges = result.getContent();
		assertExtensionWithExtensionStatus(exchanges, 1, ExtensionStatus.APPROVED);

		Userbook targetbook = exchanges.get(0).getRental().getUserbook();
		assertThatUserbookMatchExactly(targetbook, mine);
	}

	@DisplayName("연장이 허가된 모든 목록을 조회한다.")
	@Test
	void findCompletedExtensionAsAllSuccess() {
		// given
		User me = createUser("me");
		User user = createUser("someone");
		userRepository.saveAll(List.of(me, user));

		Userbook mine = createUserbook(me, "001");
		Userbook userbook = createUserbook(user, "002");
		userbookRepository.saveAll(List.of(mine, userbook));

		Rental rentalAsSender = createRental(me, userbook, RentalStatus.OFFERING);
		Rental rentalAsReceiver = createRental(user, mine, RentalStatus.OFFERING);
		rentalRepository.saveAll(List.of(rentalAsSender, rentalAsReceiver));

		Extension extensionAsSender = createExtension(rentalAsSender, ExtensionStatus.APPROVED);
		Extension extensionAsReceiver = createExtension(rentalAsReceiver, ExtensionStatus.APPROVED);
		extensionRepository.saveAll(List.of(extensionAsSender, extensionAsReceiver));

		// when
		Page<Extension> result = extensionRepository.findByStatusAndUserCondition(me.getId(), ExtensionStatus.APPROVED,
			SENDER_RECEIVER, PageRequest.of(0, 10));

		///then
		List<Extension> exchanges = result.getContent();
		assertExtensionWithExtensionStatus(exchanges, 2, ExtensionStatus.APPROVED);
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

	private Extension createExtension(Rental rental, ExtensionStatus extensionStatus) {
		return Extension.builder()
			.rental(rental)
			.createdAt(LocalDateTime.now())
			.extensionStatus(extensionStatus)
			.build();
	}

	private void assertExtensionWithExtensionStatus(List<Extension> resultContent, int size,
		ExtensionStatus rentalStatus) {
		assertThat(resultContent)
			.hasSize(size)
			.extracting("extensionStatus").contains(rentalStatus);
	}

	private void assertThatUserbookMatchExactly(Userbook target, Userbook pair) {
		assertThat(target)
			.extracting("id", "qualityStatus", "registerType", "tradeStatus")
			.containsExactlyInAnyOrder(pair.getId(), pair.getQualityStatus(), pair.getRegisterType(),
				pair.getTradeStatus());
	}
}