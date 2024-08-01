package com.e207.woojoobook.domain.exchange;

import static com.e207.woojoobook.domain.exchange.ExchangeStatus.*;
import static com.e207.woojoobook.domain.exchange.TradeUserCondition.*;
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
class ExchangeRepositoryTest {

	@Autowired
	ExchangeRepository exchangeRepository;
	@Autowired
	private UserbookRepository userbookRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BookRepository bookRepository;

	@DisplayName("교환 조회 시, 사용자 등록 도서도 함께 조회한다.")
	@Test
	void findByIdWithUserbookAndUser() {
		// given
		User me = createUser("me");
		User user = createUser("someone");
		userRepository.saveAll(List.of(me, user));

		Userbook mine = createUserbook(me, "001");
		Userbook userbook = createUserbook(user, "002");
		userbookRepository.saveAll(List.of(mine, userbook));

		Exchange exchange = createExchange(mine, userbook);
		exchangeRepository.save(exchange);

		// when
		Exchange result = exchangeRepository.findByIdWithUserbookAndUser(exchange.getId()).get();

		///then
		assertThatUserbookMatchExactly(result.getSenderBook(), mine);
		assertThatUserbookMatchExactly(result.getReceiverBook(), userbook);

		Book senderBookInfo = result.getSenderBook().getBook();
		Book receiverBookInfo = result.getReceiverBook().getBook();
		assertThatBookMatchExactly(senderBookInfo, mine.getBook());
		assertThatBookMatchExactly(receiverBookInfo, userbook.getBook());
	}

	// TODO <jhl221123> 변경된 쿼리 테스트로 변경 필요
	@DisplayName("수락된 교환 목록을 조회한다.")
	@Test
	void findCompletedExchangeSuccess() {
		// given
		User me = createUser("me");
		User user = createUser("someone");
		userRepository.saveAll(List.of(me, user));

		Userbook mine = createUserbook(me, "001");
		Userbook userbook = createUserbook(user, "002");
		userbookRepository.saveAll(List.of(mine, userbook));

		Exchange exchange = createExchange(mine, userbook);
		Exchange approvedExchange = createExchange(mine, userbook);
		Exchange rejectedExchange = createExchange(mine, userbook);
		approvedExchange.respond(APPROVED);
		rejectedExchange.respond(REJECTED);
		exchangeRepository.saveAll(List.of(exchange, approvedExchange, rejectedExchange));

		// when
		Page<Exchange> result = exchangeRepository.findAllByExchangeStatus(APPROVED, PageRequest.of(0, 10));

		///then
		List<Exchange> exchanges = result.getContent();
		assertExchangeStatus(exchanges, 1, APPROVED);
	}

	@DisplayName("거절된 교환 목록을 조회한다.")
	@Test
	void findRejectedExchangeSuccess() {
		// given
		User me = createUser("me");
		User user = createUser("someone");
		userRepository.saveAll(List.of(me, user));

		Userbook mine = createUserbook(me, "001");
		Userbook userbook = createUserbook(user, "002");
		userbookRepository.saveAll(List.of(mine, userbook));

		Exchange exchange = createExchange(mine, userbook);
		Exchange approvedExchange = createExchange(mine, userbook);
		Exchange rejectedExchange = createExchange(mine, userbook);
		approvedExchange.respond(APPROVED);
		rejectedExchange.respond(REJECTED);
		exchangeRepository.saveAll(List.of(exchange, approvedExchange, rejectedExchange));

		// when
		Page<Exchange> result = exchangeRepository.findAllByExchangeStatus(REJECTED, PageRequest.of(0, 10));

		///then
		List<Exchange> exchanges = result.getContent();
		assertExchangeStatus(exchanges, 1, REJECTED);
	}

	@DisplayName("교환 신청한 목록을 조회한다.")
	@Test
	void findExchangeOfferAsSenderSuccess() {
		// given
		User me = createUser("me");
		User user = createUser("someone");
		userRepository.saveAll(List.of(me, user));

		Userbook mine = createUserbook(me, "001");
		Userbook userbook = createUserbook(user, "002");
		userbookRepository.saveAll(List.of(mine, userbook));

		Exchange exchangeAsSender = createExchange(mine, userbook);
		Exchange exchangeAsReceiver = createExchange(userbook, mine);
		Exchange completedExchange = createExchange(mine, userbook);
		completedExchange.respond(APPROVED);
		exchangeRepository.saveAll(List.of(exchangeAsSender, exchangeAsReceiver, completedExchange));

		// when
		Page<Exchange> result = exchangeRepository.findByStatusAndUserCondition(me.getId(), IN_PROGRESS, SENDER,
			PageRequest.of(0, 10));

		///then
		List<Exchange> exchanges = result.getContent();
		assertExchangeStatus(exchanges, 1, IN_PROGRESS);

		Userbook senderBook = exchanges.get(0).getSenderBook();
		assertThatUserbookMatchExactly(senderBook, mine);
	}

	@DisplayName("교환 신청을 받은 목록을 조회한다.")
	@Test
	void findExchangeOfferAsReceiverSuccess() {
		// given
		User me = createUser("me");
		User user = createUser("someone");
		userRepository.saveAll(List.of(me, user));

		Userbook mine = createUserbook(me, "001");
		Userbook userbook = createUserbook(user, "002");
		userbookRepository.saveAll(List.of(mine, userbook));

		Exchange exchangeAsSender = createExchange(mine, userbook);
		Exchange exchangeAsReceiver = createExchange(userbook, mine);
		Exchange completedExchange = createExchange(mine, userbook);
		completedExchange.respond(APPROVED);
		exchangeRepository.saveAll(List.of(exchangeAsSender, exchangeAsReceiver, completedExchange));

		// when
		Page<Exchange> result = exchangeRepository.findByStatusAndUserCondition(me.getId(), IN_PROGRESS,
			RECEIVER, PageRequest.of(0, 10));

		List<Exchange> exchanges = result.getContent();
		assertExchangeStatus(exchanges, 1, IN_PROGRESS);

		Userbook receiverBook = exchanges.get(0).getReceiverBook();
		assertThatUserbookMatchExactly(receiverBook, mine);
	}

	@DisplayName("모든 교환 신청 목록을 조회한다.")
	@Test
	void findExchangeOfferAsAllSuccess() {
		// given
		User me = createUser("me");
		User user = createUser("someone");
		userRepository.saveAll(List.of(me, user));

		Userbook mine = createUserbook(me, "001");
		Userbook userbook = createUserbook(user, "002");
		userbookRepository.saveAll(List.of(mine, userbook));

		Exchange exchangeAsSender = createExchange(mine, userbook);
		Exchange exchangeAsReceiver = createExchange(userbook, mine);
		Exchange completedExchange = createExchange(mine, userbook);
		completedExchange.respond(APPROVED);
		exchangeRepository.saveAll(List.of(exchangeAsSender, exchangeAsReceiver, completedExchange));

		// when
		Page<Exchange> result = exchangeRepository.findByStatusAndUserCondition(me.getId(), IN_PROGRESS,
			SENDER_RECEIVER, PageRequest.of(0, 10));

		///then
		List<Exchange> exchanges = result.getContent();
		assertExchangeStatus(exchanges, 2, IN_PROGRESS);
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
			.registerType(EXCHANGE)
			.tradeStatus(EXCHANGE_AVAILABLE)
			.build();
	}

	private Exchange createExchange(Userbook senderBook, Userbook receiverBook) {
		return Exchange.builder()
			.sender(senderBook.getUser())
			.receiver(receiverBook.getUser())
			.senderBook(senderBook)
			.receiverBook(receiverBook)
			.build();
	}

	private void assertExchangeStatus(List<Exchange> resultContent, int size, ExchangeStatus exchangeStatus) {
		assertThat(resultContent)
			.hasSize(size)
			.extracting("exchangeStatus").contains(exchangeStatus);
	}

	private void assertThatUserbookMatchExactly(Userbook target, Userbook pair) {
		assertThat(target)
			.extracting("id", "qualityStatus", "registerType", "tradeStatus")
			.containsExactlyInAnyOrder(pair.getId(), pair.getQualityStatus(), pair.getRegisterType(),
				pair.getTradeStatus());
	}

	private void assertThatBookMatchExactly(Book target, Book pair) {
		assertThat(target)
			.extracting("isbn", "title", "author", "publisher", "publicationDate", "thumbnail", "description")
			.containsExactly(pair.getIsbn(), pair.getTitle(), pair.getAuthor(), pair.getPublisher(),
				pair.getPublicationDate(), pair.getThumbnail(), pair.getDescription());
	}
}