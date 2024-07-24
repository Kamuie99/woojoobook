package com.e207.woojoobook.domain.exchange;

import static com.e207.woojoobook.domain.userbook.QualityStatus.*;
import static com.e207.woojoobook.domain.userbook.RegisterType.*;
import static com.e207.woojoobook.domain.userbook.TradeStatus.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.e207.woojoobook.domain.book.Book;
import com.e207.woojoobook.domain.book.BookRepository;
import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.user.UserRepository;
import com.e207.woojoobook.domain.userbook.QualityStatus;
import com.e207.woojoobook.domain.userbook.RegisterType;
import com.e207.woojoobook.domain.userbook.TradeStatus;
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
	void findFetchById() {
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
		Exchange result = exchangeRepository.findFetchById(exchange.getId()).get();

		///then
		assertThat(result.getSenderBook())
			.extracting("id", "qualityStatus", "registerType", "tradeStatus")
			.containsExactlyInAnyOrder(senderBook.getId(), GOOD, EXCHANGE, EXCHANGE_AVAILABLE);
		assertThat(result.getReceiverBook())
			.extracting("id", "qualityStatus", "registerType", "tradeStatus")
			.containsExactlyInAnyOrder(receiverBook.getId(), GOOD, EXCHANGE, EXCHANGE_AVAILABLE);

		Book senderBookInfo = result.getSenderBook().getBook();
		Book receiverBookInfo = result.getReceiverBook().getBook();
		assertThat(senderBookInfo)
			.extracting("isbn", "title", "author", "publisher", "publicationDate", "thumbnail", "description")
			.containsExactly("001", "title1", "author1", "publisher1", publicationDate, "thumbnail1", "desc1");
		assertThat(receiverBookInfo)
			.extracting("isbn", "title", "author", "publisher", "publicationDate", "thumbnail", "description")
			.containsExactly("002", "title2", "author2", "publisher2", publicationDate, "thumbnail2", "desc2");
	}

	private Exchange createExchange(Userbook senderBook, Userbook receiverBook) {
		return Exchange.builder()
			.senderBook(senderBook)
			.receiverBook(receiverBook)
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