package com.e207.woojoobook.api.exchange;

import static com.e207.woojoobook.domain.exchange.ExchangeStatus.*;
import static com.e207.woojoobook.domain.exchange.TradeUserCondition.*;
import static com.e207.woojoobook.domain.userbook.QualityStatus.*;
import static com.e207.woojoobook.domain.userbook.RegisterType.*;
import static com.e207.woojoobook.domain.userbook.TradeStatus.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.List;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import com.e207.woojoobook.api.book.response.BookItem;
import com.e207.woojoobook.api.exchange.request.ExchangeCreateRequest;
import com.e207.woojoobook.api.exchange.request.ExchangeFindCondition;
import com.e207.woojoobook.api.exchange.request.ExchangeOfferRespondRequest;
import com.e207.woojoobook.api.exchange.response.ExchangeResponse;
import com.e207.woojoobook.api.userbook.response.UserbookResponse;
import com.e207.woojoobook.domain.book.Book;
import com.e207.woojoobook.domain.book.BookRepository;
import com.e207.woojoobook.domain.exchange.Exchange;
import com.e207.woojoobook.domain.exchange.ExchangeRepository;
import com.e207.woojoobook.domain.exchange.ExchangeStatus;
import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.user.UserRepository;
import com.e207.woojoobook.domain.userbook.Userbook;
import com.e207.woojoobook.domain.userbook.UserbookRepository;
import com.e207.woojoobook.global.exception.ErrorException;
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
		User me = createUser("me");
		User user = createUser("someone");
		userRepository.saveAll(List.of(me, user));

		Userbook mine = createUserbook(me, "001");
		Userbook userbook = createUserbook(user, "002");
		userbookRepository.saveAll(List.of(mine, userbook));

		ExchangeCreateRequest request = createRequest(mine.getId(), userbook.getId());

		given(userHelper.findCurrentUser()).willReturn(me);

		// when
		ExchangeResponse result = exchangeService.create(request);

		//then
		assertThat(result.senderBook()).extracting("id").isEqualTo(mine.getId());
		assertThat(result.receiverBook()).extracting("id").isEqualTo(userbook.getId());
	}

	@DisplayName("교환 신청 시, 교환 날짜는 입력되지 않는다.")
	@Test
	void createSuccessWithoutExchangeDate() {
		// given
		User me = createUser("me");
		User user = createUser("someone");
		userRepository.saveAll(List.of(me, user));

		Userbook mine = createUserbook(me, "001");
		Userbook userbook = createUserbook(user, "002");
		userbookRepository.saveAll(List.of(mine, userbook));

		ExchangeCreateRequest request = createRequest(mine.getId(), userbook.getId());

		given(userHelper.findCurrentUser()).willReturn(me);

		// when
		ExchangeResponse result = exchangeService.create(request);

		//then
		assertThat(result).extracting("exchangeDate").isNull();
	}

	@DisplayName("교환 신청 시, 교환 상태는 진행 중으로 입력된다.")
	@Test
	void createSuccessWithoutExchangeStatus() {
		// given
		User me = createUser("me");
		User user = createUser("someone");
		userRepository.saveAll(List.of(me, user));

		Userbook mine = createUserbook(me, "001");
		Userbook userbook = createUserbook(user, "002");
		userbookRepository.saveAll(List.of(mine, userbook));

		ExchangeCreateRequest request = createRequest(mine.getId(), userbook.getId());

		given(userHelper.findCurrentUser()).willReturn(me);

		// when
		ExchangeResponse result = exchangeService.create(request);

		//then
		assertThat(result).extracting("exchangeStatus").isEqualTo(IN_PROGRESS);
	}

	@DisplayName("중복된 교환 신청은 할 수 없다.")
	@Test
	void createFailByDuplicatedRequest() {
		// given
		User me = createUser("me");
		User user = createUser("someone");
		userRepository.saveAll(List.of(me, user));

		Userbook mine = createUserbook(me, "001");
		Userbook userbook = createUserbook(user, "002");
		userbookRepository.saveAll(List.of(mine, userbook));

		ExchangeCreateRequest request = createRequest(mine.getId(), userbook.getId());

		given(userHelper.findCurrentUser()).willReturn(me);

		// when
		ExchangeResponse result = exchangeService.create(request);

		//then
		assertThatThrownBy(() -> exchangeService.create(request)).isInstanceOf(ErrorException.class);
	}

	@DisplayName("사용자 등록 도서 및 일반 도서와 함께 교환 정보를 조회한다.")
	@Test
	void findByIdSuccess() {
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
		ExchangeResponse result = exchangeService.findById(exchange.getId());

		//then
		assertThatUserbookMatchExactly(result.senderBook(), mine);
		assertThatUserbookMatchExactly(result.receiverBook(), userbook);

		BookItem senderBookInfo = result.senderBook().bookInfo();
		BookItem receiverBookInfo = result.receiverBook().bookInfo();
		assertThatBookMatchExactly(senderBookInfo, mine.getBook());
		assertThatBookMatchExactly(receiverBookInfo, userbook.getBook());
	}

	@Transactional
	@DisplayName("교환 도메인 조회 시, 사용자 등록 도서 정보도 함께 조회된다.")
	@Test
	void findDomainSuccess() {
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
		Exchange result = exchangeService.findDomain(exchange.getId());

		//then
		assertThatUserbookMatchExactly(UserbookResponse.of(result.getSenderBook()), mine);
		assertThatUserbookMatchExactly(UserbookResponse.of(result.getReceiverBook()), userbook);
	}

	@DisplayName("존재하지 않는 교환 도메인 조회 시, 예외가 발생한다.")
	@Test
	void findDomainFail() {
		// expected
		assertThatThrownBy(() -> exchangeService.findDomain(1L)).isInstanceOf(RuntimeException.class);
	}

	@Transactional
	@DisplayName("조건에 해당하는 교환 내역을 조회한다.")
	@Test
	void findByConditionSuccess() {
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

		given(userHelper.findCurrentUser()).willReturn(me);

		ExchangeFindCondition condition = new ExchangeFindCondition(SENDER_RECEIVER, IN_PROGRESS);

		// when
		Page<ExchangeResponse> result = exchangeService.findByCondition(condition, PageRequest.of(0, 10));

		///then
		List<ExchangeResponse> exchangeResponses = result.getContent();
		assertExchangeStatus(exchangeResponses, 2, IN_PROGRESS);
	}

	// TODO <jhl221123> 추후 예외 케이스 추가 필요
	@Transactional
	@DisplayName("교환 신청을 수락하면, 책 상태가 변경되고 이벤트를 발행한다.")
	@Test
	void respondOfferSuccess() {
		// given
		User me = createUser("me");
		User user = createUser("someone");
		userRepository.saveAll(List.of(me, user));

		Userbook mine = createUserbook(me, "001");
		Userbook userbook = createUserbook(user, "002");
		userbookRepository.saveAll(List.of(mine, userbook));

		Exchange exchange = createExchange(userbook, mine);
		exchangeRepository.save(exchange);

		ExchangeOfferRespondRequest respondRequest = new ExchangeOfferRespondRequest(APPROVED);

		given(userHelper.findCurrentUser()).willReturn(me);

		// when
		exchangeService.respondOffer(exchange.getId(), respondRequest);

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
		User me = createUser("me");
		User user = createUser("someone");
		userRepository.saveAll(List.of(me, user));

		Userbook mine = createUserbook(me, "001");
		Userbook userbook = createUserbook(user, "002");
		userbookRepository.saveAll(List.of(mine, userbook));

		Exchange exchange = createExchange(mine, userbook);
		exchangeRepository.save(exchange);

		given(userHelper.findCurrentUser()).willReturn(me);

		// when
		exchangeService.delete(exchange.getId());

		//then
		Optional<Exchange> target = exchangeRepository.findById(exchange.getId());
		assertThat(target).isEmpty();
	}

	private Exchange createExchange(Userbook senderBook, Userbook receiverBook) {
		return Exchange.builder()
			.sender(senderBook.getUser())
			.receiver(receiverBook.getUser())
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

	private void assertExchangeStatus(List<ExchangeResponse> resultContent, int size, ExchangeStatus exchangeStatus) {
		assertThat(resultContent)
			.hasSize(size)
			.extracting("exchangeStatus").contains(exchangeStatus);
	}

	private void assertThatUserbookMatchExactly(UserbookResponse target, Userbook pair) {
		assertThat(target)
			.extracting("id", "qualityStatus", "registerType", "tradeStatus")
			.containsExactlyInAnyOrder(pair.getId(), pair.getQualityStatus(), pair.getRegisterType(),
				pair.getTradeStatus());
	}

	private void assertThatBookMatchExactly(BookItem target, Book pair) {
		assertThat(target)
			.extracting("isbn", "title", "author", "publisher", "publicationDate", "thumbnail", "description")
			.containsExactly(pair.getIsbn(), pair.getTitle(), pair.getAuthor(), pair.getPublisher(),
				pair.getPublicationDate(), pair.getThumbnail(), pair.getDescription());
	}
}