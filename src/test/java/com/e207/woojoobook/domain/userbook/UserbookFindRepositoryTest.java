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
import org.springframework.data.domain.PageRequest;

import net.bytebuddy.utility.RandomString;

import com.e207.woojoobook.domain.book.Book;
import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.global.util.DynamicQueryHelper;

@Import({DynamicQueryHelper.class})
@DataJpaTest
class UserbookFindRepositoryTest {

	@Autowired
	UserbookRepository userbookRepository;
	@Autowired
	TestEntityManager em;

	@DisplayName("키워드를 입력하면 제목, 저자에 키워드가 포함된 결과를 반환한다.")
	@Test
	void findUserbookPageListByKeyword() {
		// given
		String expectedKeyword = "우주";

		UserbookFindCondition condition = new UserbookFindCondition(expectedKeyword, List.of(), null);

		List<Book> bookList = List.of(createBookByKeywordInTitle(expectedKeyword),
			createBookByKeywordInAuthor(expectedKeyword), createBookByKeywordInTitle("지구"));
		bookList.forEach(em::persist);

		List<User> userList = Stream.generate(this::createRandomUser).limit(3).toList();
		userList.forEach(em::persist);

		RegisterType registerRental = RegisterType.RENTAL;
		TradeStatus canRent = TradeStatus.RENTAL_AVAILABLE;

		List<Userbook> userbookList = List.of(createUserbook(userList.get(0), bookList.get(0), registerRental, canRent),
			createUserbook(userList.get(1), bookList.get(1), registerRental, canRent),
			createUserbook(userList.get(2), bookList.get(2), registerRental, canRent));
		userbookList.forEach(em::persist);

		// when
		Page<Userbook> pageResult = userbookRepository.findUserbookListByPage(condition, PageRequest.of(0, 10));
		List<Userbook> result = pageResult.getContent();

		// then
		assertThat(result).isNotEmpty();
		assertThat(result).map(Userbook::getBook)
			.allMatch(book -> book.getTitle().contains(expectedKeyword) || book.getAuthor().contains(expectedKeyword));
	}

	@DisplayName("지역 코드 목록을 입력하면 지역코드 목록에 포함된 사용자 도서만 반환한다.")
	@Test
	void findUserbookPageListByAreaCode() {
		// given
		List<String> expectedAreaCodeList = List.of("대구", "대전");
		UserbookFindCondition condition = new UserbookFindCondition(null, expectedAreaCodeList, null);

		List<Book> bookList = Stream.generate(this::createRandomBook).limit(5).toList();
		bookList.forEach(em::persist);

		List<User> userList = List.of(createUserByAreaCode("부산"), createUserByAreaCode("부산"),
			createUserByAreaCode("대구"), createUserByAreaCode("대전"), createUserByAreaCode("대전"));
		userList.forEach(em::persist);

		RegisterType registerRental = RegisterType.RENTAL;
		TradeStatus canRent = TradeStatus.RENTAL_AVAILABLE;

		List<Userbook> userbookList = List.of(createUserbook(userList.get(0), bookList.get(0), registerRental, canRent),
			createUserbook(userList.get(1), bookList.get(1), registerRental, canRent),
			createUserbook(userList.get(2), bookList.get(2), registerRental, canRent),
			createUserbook(userList.get(3), bookList.get(3), registerRental, canRent),
			createUserbook(userList.get(4), bookList.get(4), registerRental, canRent));
		userbookList.forEach(em::persist);

		// when
		Page<Userbook> pageResult = userbookRepository.findUserbookListByPage(condition, PageRequest.of(0, 10));
		List<Userbook> result = pageResult.getContent();

		// then
		assertThat(result).isNotEmpty();
		assertThat(result).map(Userbook::getAreaCode).allMatch(expectedAreaCodeList::contains);
	}

	@DisplayName("등록 타입을 입력하면 동일한 등록 타입에 거래 가능한 사용자 도서만 반환한다.")
	@Test
	void findUserbookPageListByRegisterType() {
		// given
		RegisterType expectedRegisterType = RegisterType.RENTAL;
		UserbookFindCondition condition = new UserbookFindCondition(null, List.of(), expectedRegisterType);

		List<Book> bookList = Stream.generate(this::createRandomBook).limit(6).toList();
		bookList.forEach(em::persist);

		List<User> userList = Stream.generate(this::createRandomUser).limit(6).toList();
		userList.forEach(em::persist);

		RegisterType registerRental = RegisterType.RENTAL;
		RegisterType registerExchange = RegisterType.EXCHANGE;
		RegisterType registerRentalAndExchange = RegisterType.RENTAL_EXCHANGE;

		TradeStatus canRentAndExchange = TradeStatus.RENTAL_EXCHANGE_AVAILABLE;
		TradeStatus canRent = TradeStatus.RENTAL_AVAILABLE;
		TradeStatus canExchange = TradeStatus.EXCHANGE_AVAILABLE;
		TradeStatus rented = TradeStatus.RENTED;
		TradeStatus unavailable = TradeStatus.UNAVAILABLE;

		List<Userbook> userbookList = List.of(createUserbook(userList.get(0), bookList.get(0), registerRental, canRent),
			createUserbook(userList.get(1), bookList.get(1), registerExchange, canExchange),
			createUserbook(userList.get(2), bookList.get(2), registerRentalAndExchange, canRentAndExchange),
			createUserbook(userList.get(3), bookList.get(3), registerRentalAndExchange, canExchange),
			createUserbook(userList.get(4), bookList.get(4), registerRental, rented),
			createUserbook(userList.get(5), bookList.get(5), registerRental, unavailable));
		userbookList.forEach(em::persist);

		// when
		Page<Userbook> pageResult = userbookRepository.findUserbookListByPage(condition, PageRequest.of(0, 10));
		List<Userbook> result = pageResult.getContent();

		// then
		assertThat(result).isNotEmpty();
		assertThat(result).map(Userbook::getRegisterType)
			.allMatch(registerTypes -> registerTypes.equals(expectedRegisterType));
		assertThat(result).map(Userbook::getTradeStatus)
			.allMatch(tradeStatus -> tradeStatus.equals(TradeStatus.RENTAL_AVAILABLE));
	}

	@DisplayName("사용자 등록 도서 조회 시, 사용자와 책 정보도 함께 조회한다.")
	@Test
	void findByIdWithUserAndBookSuccess() {
		// TODO <jhl221123> 테스트
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