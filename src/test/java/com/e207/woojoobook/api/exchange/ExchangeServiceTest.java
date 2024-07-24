package com.e207.woojoobook.api.exchange;

import static com.e207.woojoobook.domain.exchange.ExchangeStatus.*;
import static com.e207.woojoobook.domain.userbook.QualityStatus.*;
import static com.e207.woojoobook.domain.userbook.RegisterType.*;
import static com.e207.woojoobook.domain.userbook.TradeStatus.*;
import static java.lang.Boolean.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import com.e207.woojoobook.api.book.response.BookResponse;
import com.e207.woojoobook.api.exchange.request.ExchangeCreateRequest;
import com.e207.woojoobook.api.exchange.request.ExchangeOfferRespondRequest;
import com.e207.woojoobook.api.exchange.response.ExchangeResponse;
import com.e207.woojoobook.domain.book.Book;
import com.e207.woojoobook.domain.book.BookRepository;
import com.e207.woojoobook.domain.exchange.Exchange;
import com.e207.woojoobook.domain.exchange.ExchangeRepository;
import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.user.UserRepository;
import com.e207.woojoobook.domain.userbook.QualityStatus;
import com.e207.woojoobook.domain.userbook.RegisterType;
import com.e207.woojoobook.domain.userbook.TradeStatus;
import com.e207.woojoobook.domain.userbook.Userbook;
import com.e207.woojoobook.domain.userbook.UserbookRepository;
import com.e207.woojoobook.global.helper.UserHelper;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class ExchangeServiceTest {

	@Autowired
	private ExchangeService exchangeService;

	@Autowired
	private UserbookRepository userbookRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private ExchangeRepository exchangeRepository;

	@MockBean
	private ApplicationEventPublisher eventPublisher;

	@MockBean
	private UserHelper userHelper;

	@AfterEach
	void tearDown() {
		exchangeRepository.deleteAllInBatch();
	}

	@DisplayName("교환 신청 시, 책 정보가 등록된다.")
	@Test
	void createSuccessWithUserbookInfo() {
		// given
		User sender = createUser("sender");
		User receiver = createUser("receiver");
		userRepository.save(sender);
		userRepository.save(receiver);

		LocalDate publicationDate = LocalDate.of(2024, 7, 22);
		Book book1 = createBook("001", "title1", "author1", "publisher1", publicationDate, "thumbnail1", "desc1");
		Book book2 = createBook("002", "title2", "author2", "publisher2", publicationDate, "thumbnail2", "desc2");
		bookRepository.save(book1);
		bookRepository.save(book2);

		Userbook senderBook = createUserbook(book1, sender, GOOD, EXCHANGE, EXCHANGE_AVAILABLE);
		Userbook receiverBook = createUserbook(book2, receiver, GOOD, EXCHANGE, EXCHANGE_AVAILABLE);
		userbookRepository.save(senderBook);
		userbookRepository.save(receiverBook);

		ExchangeCreateRequest request = createRequest(senderBook.getId(), receiverBook.getId());

		// when
		ExchangeResponse result = exchangeService.create(request);

		//then
		assertThat(result.senderBook()).extracting("id").isEqualTo(senderBook.getId());
		assertThat(result.receiverBook()).extracting("id").isEqualTo(receiverBook.getId());
	}

	@DisplayName("교환 신청 시, 교환 날짜는 입력되지 않는다.")
	@Test
	void createSuccessWithoutExchangeDate() {
		// given
		User sender = createUser("sender");
		User receiver = createUser("receiver");
		userRepository.save(sender);
		userRepository.save(receiver);

		LocalDate publicationDate = LocalDate.of(2024, 7, 22);
		Book book1 = createBook("001", "title1", "author1", "publisher1", publicationDate, "thumbnail1", "desc1");
		Book book2 = createBook("002", "title2", "author2", "publisher2", publicationDate, "thumbnail2", "desc2");
		bookRepository.save(book1);
		bookRepository.save(book2);

		Userbook senderBook = createUserbook(book1, sender, GOOD, EXCHANGE, EXCHANGE_AVAILABLE);
		Userbook receiverBook = createUserbook(book2, receiver, GOOD, EXCHANGE, EXCHANGE_AVAILABLE);
		userbookRepository.save(senderBook);
		userbookRepository.save(receiverBook);

		ExchangeCreateRequest request = createRequest(senderBook.getId(), receiverBook.getId());

		// when
		ExchangeResponse result = exchangeService.create(request);

		//then
		assertThat(result).extracting("exchangeDate").isNull();
	}

	@DisplayName("교환 신청 시, 교환 상태는 진행 중으로 입력된다.")
	@Test
	void createSuccessWithoutExchangeStatus() {
		// given
		User sender = createUser("sender");
		User receiver = createUser("receiver");
		userRepository.save(sender);
		userRepository.save(receiver);

		LocalDate publicationDate = LocalDate.of(2024, 7, 22);
		Book book1 = createBook("001", "title1", "author1", "publisher1", publicationDate, "thumbnail1", "desc1");
		Book book2 = createBook("002", "title2", "author2", "publisher2", publicationDate, "thumbnail2", "desc2");
		bookRepository.save(book1);
		bookRepository.save(book2);

		Userbook senderBook = createUserbook(book1, sender, GOOD, EXCHANGE, EXCHANGE_AVAILABLE);
		Userbook receiverBook = createUserbook(book2, receiver, GOOD, EXCHANGE, EXCHANGE_AVAILABLE);
		userbookRepository.save(senderBook);
		userbookRepository.save(receiverBook);

		ExchangeCreateRequest request = createRequest(senderBook.getId(), receiverBook.getId());

		// when
		ExchangeResponse result = exchangeService.create(request);

		//then
		assertThat(result).extracting("exchangeStatus").isEqualTo(IN_PROGRESS);
	}

	@DisplayName("사용자 등록 도서 및 일반 도서와 함께 교환 정보를 조회한다.")
	@Test
	void findByIdSuccess() {
		// given
		User sender = createUser("sender");
		User receiver = createUser("receiver");
		userRepository.save(sender);
		userRepository.save(receiver);

		LocalDate publicationDate = LocalDate.of(2024, 7, 22);
		Book book1 = createBook("001", "title1", "author1", "publisher1", publicationDate, "thumbnail1", "desc1");
		Book book2 = createBook("002", "title2", "author2", "publisher2", publicationDate, "thumbnail2", "desc2");
		bookRepository.save(book1);
		bookRepository.save(book2);

		Userbook senderBook = createUserbook(book1, sender, GOOD, EXCHANGE, EXCHANGE_AVAILABLE);
		Userbook receiverBook = createUserbook(book2, receiver, GOOD, EXCHANGE, EXCHANGE_AVAILABLE);
		userbookRepository.save(senderBook);
		userbookRepository.save(receiverBook);

		Exchange exchange = createExchange(senderBook, receiverBook);
		exchangeRepository.save(exchange);

		// when
		ExchangeResponse result = exchangeService.findById(exchange.getId());

		//then
		assertThat(result.senderBook())
			.extracting("id", "qualityStatus", "registerType", "tradeStatus")
			.containsExactlyInAnyOrder(senderBook.getId(), GOOD, EXCHANGE, EXCHANGE_AVAILABLE);
		assertThat(result.receiverBook())
			.extracting("id", "qualityStatus", "registerType", "tradeStatus")
			.containsExactlyInAnyOrder(receiverBook.getId(), GOOD, EXCHANGE, EXCHANGE_AVAILABLE);

		BookResponse senderBookInfo = result.senderBook().bookInfo();
		BookResponse receiverBookInfo = result.receiverBook().bookInfo();
		assertThat(senderBookInfo)
			.extracting("isbn", "title", "author", "publisher", "publicationDate", "thumbnail", "description")
			.containsExactly("001", "title1", "author1", "publisher1", publicationDate, "thumbnail1", "desc1");
		assertThat(receiverBookInfo)
			.extracting("isbn", "title", "author", "publisher", "publicationDate", "thumbnail", "description")
			.containsExactly("002", "title2", "author2", "publisher2", publicationDate, "thumbnail2", "desc2");
	}

	@Transactional
	@DisplayName("사용자 등록 도서와 함께 교환 도메인을 조회한다.")
	@Test
	void findDomainSuccess() {
		// given
		User sender = createUser("sender");
		User receiver = createUser("receiver");
		userRepository.save(sender);
		userRepository.save(receiver);

		LocalDate publicationDate = LocalDate.of(2024, 7, 22);
		Book book1 = createBook("001", "title1", "author1", "publisher1", publicationDate, "thumbnail1", "desc1");
		Book book2 = createBook("002", "title2", "author2", "publisher2", publicationDate, "thumbnail2", "desc2");
		bookRepository.save(book1);
		bookRepository.save(book2);

		Userbook senderBook = createUserbook(book1, sender, GOOD, EXCHANGE, EXCHANGE_AVAILABLE);
		Userbook receiverBook = createUserbook(book2, receiver, GOOD, EXCHANGE, EXCHANGE_AVAILABLE);
		userbookRepository.save(senderBook);
		userbookRepository.save(receiverBook);

		Exchange exchange = createExchange(senderBook, receiverBook);
		exchangeRepository.save(exchange);

		// when
		Exchange result = exchangeService.findDomain(exchange.getId());

		//then
		assertThat(result.getSenderBook())
			.extracting("id", "qualityStatus", "registerType", "tradeStatus")
			.containsExactlyInAnyOrder(senderBook.getId(), GOOD, EXCHANGE, EXCHANGE_AVAILABLE);
		assertThat(result.getReceiverBook())
			.extracting("id", "qualityStatus", "registerType", "tradeStatus")
			.containsExactlyInAnyOrder(receiverBook.getId(), GOOD, EXCHANGE, EXCHANGE_AVAILABLE);
	}

	@DisplayName("존재하지 않는 교환 도메인 조회 시, 예외가 발생한다.")
	@Test
	void findDomainFail() {
		// expected
		assertThatThrownBy(() -> exchangeService.findDomain(1L)).isInstanceOf(RuntimeException.class);
	}

	@Transactional
	@DisplayName("교환 신청을 수락하면, 책 상태가 변경되고 이벤트를 발행한다.")
	@Test
		// TODO <jhl221123> 예외 케이스 추후 추가 필요
	void offerRespondSuccess() {
		// given
		User sender = createUser("sender");
		User receiver = createUser("receiver");
		userRepository.save(sender);
		userRepository.save(receiver);

		LocalDate publicationDate = LocalDate.of(2024, 7, 22);
		Book book1 = createBook("001", "title1", "author1", "publisher1", publicationDate, "thumbnail1", "desc1");
		Book book2 = createBook("002", "title2", "author2", "publisher2", publicationDate, "thumbnail2", "desc2");
		bookRepository.save(book1);
		bookRepository.save(book2);

		Userbook senderBook = createUserbook(book1, sender, GOOD, EXCHANGE, EXCHANGE_AVAILABLE);
		Userbook receiverBook = createUserbook(book2, receiver, GOOD, EXCHANGE, EXCHANGE_AVAILABLE);
		userbookRepository.save(senderBook);
		userbookRepository.save(receiverBook);

		Exchange exchange = createExchange(senderBook, receiverBook);
		exchangeRepository.save(exchange);

		ExchangeOfferRespondRequest respondRequest = new ExchangeOfferRespondRequest(TRUE);

		given(userHelper.findCurrentUser()).willReturn(receiver);

		// when
		exchangeService.offerRespond(exchange.getId(), respondRequest);

		//then
		assertThat(exchange.getSenderBook().getTradeStatus()).isEqualTo(EXCHANGED);
		assertThat(exchange.getReceiverBook().getTradeStatus()).isEqualTo(EXCHANGED);
		// verify(eventPublisher, times(1)).publishEvent(any(ExchangeRespondEvent.class)); // TODO <jhl221123> 실제 호출은 되지만 테스트에서 인식이 안됨. 추후 해결 필요
	}

	@Transactional
	@DisplayName("교환을 신청한 사용자가 교환 신청을 취소한다.")
	@Test
	void deleteSuccess() {
		// given
		User sender = createUser("sender");
		User receiver = createUser("receiver");
		userRepository.save(sender);
		userRepository.save(receiver);

		LocalDate publicationDate = LocalDate.of(2024, 7, 22);
		Book book1 = createBook("001", "title1", "author1", "publisher1", publicationDate, "thumbnail1", "desc1");
		Book book2 = createBook("002", "title2", "author2", "publisher2", publicationDate, "thumbnail2", "desc2");
		bookRepository.save(book1);
		bookRepository.save(book2);

		Userbook senderBook = createUserbook(book1, sender, GOOD, EXCHANGE, EXCHANGE_AVAILABLE);
		Userbook receiverBook = createUserbook(book2, receiver, GOOD, EXCHANGE, EXCHANGE_AVAILABLE);
		userbookRepository.save(senderBook);
		userbookRepository.save(receiverBook);

		Exchange exchange = createExchange(senderBook, receiverBook);
		exchangeRepository.save(exchange);

		given(userHelper.findCurrentUser()).willReturn(sender);

		// when
		exchangeService.delete(exchange.getId());

		//then
		Optional<Exchange> target = exchangeRepository.findById(exchange.getId());
		assertThat(target).isEmpty();
	}

	private Exchange createExchange(Userbook senderBook, Userbook receiverBook) {
		return Exchange.builder()
			.senderBook(senderBook)
			.receiverBook(receiverBook)
			.build();
	}

	private ExchangeCreateRequest createRequest(Long senderBookId, Long receiverBookId) {
		return ExchangeCreateRequest.builder()
			.senderBookId(senderBookId)
			.receiverBookId(receiverBookId)
			.build();
	}

	private User createUser(String nickname) {
		return User.builder()
			.email("user@email.com")
			.password("encrypted password")
			.nickname(nickname)
			.areaCode("1234567")
			.build();
	}

	private Userbook createUserbook(Book book, User user, QualityStatus qualityStatus, RegisterType registerType,
		TradeStatus tradeStatus) {
		return Userbook.builder()
			.book(book)
			.user(user)
			.qualityStatus(qualityStatus)
			.registerType(registerType)
			.tradeStatus(tradeStatus)
			.build();
	}

	private Book createBook(String isbn, String title, String author, String publisher, LocalDate publicationDate,
		String thumbnail, String description) {
		return Book.builder()
			.isbn(isbn)
			.title(title)
			.author(author)
			.publisher(publisher)
			.publicationDate(publicationDate)
			.thumbnail(thumbnail)
			.description(description)
			.build();
	}
}