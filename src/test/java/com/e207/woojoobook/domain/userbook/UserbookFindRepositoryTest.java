package com.e207.woojoobook.domain.userbook;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import net.bytebuddy.utility.RandomString;

import com.e207.woojoobook.api.userbook.request.UserbookListRequest;
import com.e207.woojoobook.domain.book.Book;
import com.e207.woojoobook.domain.user.User;

@Import(UserbookQueryRepository.class)
@DataJpaTest
class UserbookQueryRepositoryTest {

	@Autowired
	UserbookQueryRepository userbookQueryRepository;
	@Autowired
	TestEntityManager em;

	@DisplayName("지역 코드를 입력하면 지역코드 목록에 포함된 사용자 도서만 반환한다.")
	@Test
	void findUserbookPageListByAreaCode() {
		// given
		List<Book> bookList = Stream.generate(this::createRandomBook).limit(5).toList();
		bookList.forEach(em::persist);

		User user = createUserByAreaCode("부산");
		em.persist(user);

		for (Book book : bookList) {
			Userbook userbook = createUserbook(user, book, RegisterType.RENTAL_EXCHANGE,
				RegisterType.RENTAL_EXCHANGE.getDefaultTradeStatus());
			em.persist(userbook);
		}

		UserbookListRequest request = new UserbookListRequest(user.getAreaCode(), null, null, 10);

		// when
		var userbookListResponse = userbookQueryRepository.findUserbookListWithLikeStatus(user.getId(), request);

		// then
		assertThat(userbookListResponse).isNotEmpty();
		assertThat(userbookListResponse.size()).isEqualTo(bookList.size());
	}

	@DisplayName("사용자 등록 도서 조회 시, 사용자와 책 정보도 함께 조회한다.")
	@Test
	void findByIdWithUserAndBookSuccess() {
		// TODO <jhl221123> 테스트
	}

	@DisplayName("내가 등록한 도서 조회 시, 거래 상태로 필터링할 수 있다.")
	@Test
	void findUserbookFilteredTradeStatus() {
		// given
		User user = createRandomUser();
		em.persist(user);
		List<Book> bookList = Stream.generate(this::createRandomBook).limit(6).map(em::persist).toList();
		bookList.stream()
			.limit(3)
			.map(book -> createUserbook(user, book, RegisterType.RENTAL, TradeStatus.RENTED))
			.forEach(em::persist);
		bookList.stream()
			.skip(3)
			.map(book -> createUserbook(user, book, RegisterType.EXCHANGE, TradeStatus.EXCHANGE_AVAILABLE))
			.forEach(em::persist);

		MyUserbookCondition condition = new MyUserbookCondition(user.getId(), TradeStatus.RENTED);

		// when
		Page<Userbook> result = userbookQueryRepository.findMyPage(condition, Pageable.ofSize(10));
		List<Userbook> content = result.getContent();

		// then
		assertThat(content).isNotEmpty().allMatch(userbook -> userbook.getTradeStatus() == TradeStatus.RENTED);
	}

	@DisplayName("내 교환 가능한 사용자 도서 조회를 할 수 있다.")
	@Test
	void findMyExchangeableUserbook() {
		// given
		User user = createRandomUser();
		em.persist(user);
		List<Book> bookList = Stream.generate(this::createRandomBook).limit(6).map(em::persist).toList();
		bookList.stream()
			.limit(3)
			.map(book -> createUserbook(user, book, RegisterType.RENTAL, TradeStatus.RENTED))
			.forEach(em::persist);
		bookList.stream()
			.skip(3)
			.map(book -> createUserbook(user, book, RegisterType.EXCHANGE, TradeStatus.EXCHANGE_AVAILABLE))
			.forEach(em::persist);

		MyExchangableUserbookCondition condition = new MyExchangableUserbookCondition(user.getId());

		// when
		Page<Userbook> result = userbookQueryRepository.findMyExchangablePage(condition, Pageable.ofSize(10));
		List<Userbook> content = result.getContent();

		// then
		assertThat(content).isNotEmpty()
			.allMatch(userbook -> userbook.getTradeStatus() == TradeStatus.EXCHANGE_AVAILABLE);
	}

	private User createUserByAreaCode(String areaCode) {
		return User.builder()
			.email(RandomString.make())
			.password(RandomString.make())
			.nickname(RandomString.make())
			.areaCode(areaCode)
			.build();
	}

	private Book createRandomBook() {
		return Book.builder()
			.isbn(UUID.randomUUID().toString())
			.title(RandomString.make())
			.author(RandomString.make())
			.build();
	}

	private User createRandomUser() {
		return User.builder()
			.email(RandomString.make())
			.nickname(RandomString.make())
			.password(RandomString.make())
			.areaCode(RandomString.make())
			.build();
	}

	private Book createBookByKeywordInTitle(String keyword) {
		String prefix = RandomString.make(5);
		String suffix = RandomString.make(5);
		String author = RandomString.make();
		String title = prefix + keyword + suffix;

		return Book.builder().isbn(UUID.randomUUID().toString()).title(title).author(author).build();
	}

	private Book createBookByKeywordInAuthor(String keyword) {
		String title = RandomString.make(5);
		String prefix = RandomString.make(5);
		String suffix = RandomString.make(5);
		String author = prefix + keyword + suffix;

		return Book.builder().isbn(UUID.randomUUID().toString()).title(title).author(author).build();
	}

	private Userbook createUserbook(User user, Book book, RegisterType registerTypes, TradeStatus tradeStatuses) {
		return Userbook.builder()
			.user(user)
			.book(book)
			.registerType(registerTypes)
			.tradeStatus(tradeStatuses)
			.qualityStatus(QualityStatus.GOOD)
			.build();
	}
}