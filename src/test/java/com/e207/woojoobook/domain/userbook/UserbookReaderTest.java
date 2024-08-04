package com.e207.woojoobook.domain.userbook;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import net.bytebuddy.utility.RandomString;

import com.e207.woojoobook.domain.book.Book;
import com.e207.woojoobook.domain.book.BookRepository;
import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.user.UserRepository;

@SpringBootTest
class UserbookReaderTest {

	@Autowired
	UserbookReader userbookReader;
	@Autowired
	UserRepository userRepository;
	@Autowired
	UserbookRepository userbookRepository;
	@Autowired
	BookRepository bookRepository;
	@Autowired
	WishbookRepository wishbookRepository;

	@DisplayName("관심 등록한 사용자 도서를 조회하면 관심 등록한 도서만 조회된다.")
	@Transactional
	@Test
	void When_FindLikedUserbook_Then_ReturnLikedUserbook() {
		// given
		List<User> userList = Stream.generate(this::createUser).limit(2).map(userRepository::save).toList();
		List<Book> bookList = Stream.generate(this::createBook).limit(5).map(bookRepository::save).toList();

		User targetUser = userList.get(0);
		User otherUser = userList.get(1);

		List<Userbook> userbookList = bookList.stream()
			.map((book) -> createUserbook(otherUser, book))
			.map(userbookRepository::save)
			.toList();

		List<Wishbook> wishbookList = userbookList.stream()
			.skip(2)
			.limit(2)
			.map(userbook -> createWishbook(targetUser, userbook))
			.map(wishbookRepository::save)
			.toList();

		List<Userbook> expectedUserbookList = wishbookList.stream().map(Wishbook::getUserbook).toList();

		// when
		Page<Userbook> result = userbookReader.findLikedPageByUser(targetUser, Pageable.ofSize(5));

		// then
		List<Userbook> content = result.getContent();
		assertThat(content).isNotEmpty();
		assertThat(content).allMatch(expectedUserbookList::contains);
	}

	@DisplayName("관심 등록한 사용자 도서가 없으면 빈 리스트를 반환한다.")
	@Transactional
	@Test
	void When_LikedUserbookIsNothing_Then_ReturnEmptyList() {
		// given
		List<User> userList = Stream.generate(this::createUser).limit(2).map(userRepository::save).toList();
		List<Book> bookList = Stream.generate(this::createBook).limit(5).map(bookRepository::save).toList();

		User targetUser = userList.get(0);
		User otherUser = userList.get(1);

		bookList.stream()
			.map((book) -> createUserbook(otherUser, book))
			.map(userbookRepository::save)
			.toList();

		// when
		Page<Userbook> result = userbookReader.findLikedPageByUser(targetUser,
			Pageable.ofSize(5));

		// then
		List<Userbook> content = result.getContent();
		assertThat(content).isEmpty();
	}

	private User createUser() {
		return User.builder().build();
	}

	private Book createBook() {
		return Book.builder().isbn(RandomString.make()).build();
	}

	private Userbook createUserbook(User owner, Book book) {
		return Userbook.builder()
			.user(owner)
			.book(book)
			.registerType(RegisterType.RENTAL_EXCHANGE)
			.tradeStatus(TradeStatus.RENTAL_EXCHANGE_AVAILABLE)
			.qualityStatus(QualityStatus.VERY_GOOD)
			.build();
	}

	private Wishbook createWishbook(User user, Userbook userbook) {
		return Wishbook.builder().user(user).userbook(userbook).build();
	}
}