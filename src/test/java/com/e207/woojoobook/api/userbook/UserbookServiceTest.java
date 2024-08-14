package com.e207.woojoobook.api.userbook;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;

import net.bytebuddy.utility.RandomString;

import com.e207.woojoobook.api.book.response.BookItem;
import com.e207.woojoobook.api.userbook.request.UserbookCreateRequest;
import com.e207.woojoobook.api.userbook.request.UserbookUpdateRequest;
import com.e207.woojoobook.api.userbook.response.UserbookResponse;
import com.e207.woojoobook.client.NaverBookSearchClient;
import com.e207.woojoobook.domain.book.Book;
import com.e207.woojoobook.domain.book.BookRepository;
import com.e207.woojoobook.domain.exchange.Exchange;
import com.e207.woojoobook.domain.exchange.ExchangeRepository;
import com.e207.woojoobook.domain.exchange.ExchangeStatus;
import com.e207.woojoobook.domain.rental.Rental;
import com.e207.woojoobook.domain.rental.RentalRepository;
import com.e207.woojoobook.domain.rental.RentalStatus;
import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.user.UserRepository;
import com.e207.woojoobook.domain.userbook.QualityStatus;
import com.e207.woojoobook.domain.userbook.RegisterType;
import com.e207.woojoobook.domain.userbook.TradeStatus;
import com.e207.woojoobook.domain.userbook.Userbook;
import com.e207.woojoobook.domain.userbook.UserbookRepository;
import com.e207.woojoobook.global.helper.UserHelper;

import jakarta.transaction.Transactional;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class UserbookServiceTest {

	@MockBean
	private UserHelper userHelper;
	@MockBean
	private NaverBookSearchClient naverBookSearchClient;
	@MockBean
	private JavaMailSender mailSender;
	@Autowired
	private UserbookService userbookService;
	@Autowired
	private BookRepository bookRepository;
	@Autowired
	private UserbookRepository userbookRepository;
	@Autowired
	private RentalRepository rentalRepository;
	@Autowired
	private ExchangeRepository exchangeRepository;
	@Autowired
	private UserRepository userRepository;
	private User currentUser;

	@BeforeEach
	public void setUp() {
		currentUser = User.builder().build();
		userRepository.save(currentUser);
		given(userHelper.findCurrentUser()).willReturn(currentUser);
	}

	@DisplayName("사용자가 등록하려는 도서가 저장되어 있지 않으면 저장 후 등록한다.")
	@Test
	void When_NotExistBook_Expect_SaveBook() {
		// given
		String expectIsbn = RandomString.make();
		Book book = Book.builder().isbn(expectIsbn).description(RandomString.make()).build();

		BookItem bookItem = BookItem.of(book);
		given(naverBookSearchClient.findBookByIsbn(any())).willReturn(Optional.of(bookItem));

		UserbookCreateRequest request = new UserbookCreateRequest(expectIsbn, RegisterType.RENTAL, QualityStatus.GOOD);

		// when
		userbookService.createUserbook(request);

		// then
		assertTrue(bookRepository.findById(expectIsbn).isPresent());
	}

	@DisplayName("사용자 도서의 등록 상태가 대여 불가로 변경되면, 모든 대여 신청은 거절된다.")
	@Transactional
	@Test
	void When_DisableRental_Expect_RejectAllOffer() {
		// given
		List<User> borrowerList = makeRandomUserList(3);

		Book book = makeRandomBookList(1).get(0);
		Userbook userbook = makeUserbookList(List.of(currentUser), List.of(book), RegisterType.RENTAL).get(0);
		makeRental(borrowerList, userbook);

		UserbookUpdateRequest request = new UserbookUpdateRequest(false, true, QualityStatus.GOOD);

		// when
		userbookService.updateUserbook(userbook.getId(), request);
		List<Rental> result = rentalRepository.findAllByUserbook(userbook);

		// then
		assertThat(result).allMatch(rental -> rental.getRentalStatus() == RentalStatus.REJECTED);
	}

	@DisplayName("사용자 도서의 등록 상태가 교환 불가로 변경되면, 모든 교환 신청은 거절된다.")
	@Transactional
	@Test
	void When_DisableExchange_Expect_RejectAllOffer() {
		// given
		List<Book> bookList = makeRandomBookList(4);
		Book receiverBook = bookList.get(0);
		List<Book> senderBookList = bookList.subList(1, 4);

		List<User> senderList = makeRandomUserList(3);

		Userbook receiverUserbook = makeUserbookList(List.of(currentUser), List.of(receiverBook),
			RegisterType.EXCHANGE).get(0);
		List<Userbook> senderUserbookList = makeUserbookList(senderList, senderBookList, RegisterType.EXCHANGE);

		makeExchange(senderUserbookList, receiverUserbook);
		UserbookUpdateRequest request = new UserbookUpdateRequest(true, false, QualityStatus.GOOD);

		// when
		userbookService.updateUserbook(receiverUserbook.getId(), request);
		List<Exchange> result = exchangeRepository.findAllByReceiverBook(receiverUserbook);

		// then
		assertThat(result).allMatch(exchange -> exchange.getExchangeStatus() == ExchangeStatus.REJECTED);
	}

	@DisplayName("사용자가 등록한 도서를 조회할 수 있다.")
	@Transactional
	@Test
	void When_FindOwnedUserbook_Expect_OwnedUserbookList() {
		// given
		User otherUser = User.builder().build();
		userRepository.save(otherUser);

		List<Book> myBookList = makeRandomBookList(3);
		List<Book> otherBookList = makeRandomBookList(3);

		List<UserbookResponse> myUserbookList = makeUserbookList(currentUser, myBookList).stream()
			.map(UserbookResponse::of)
			.toList();
		List<UserbookResponse> otherUserbookList = makeUserbookList(otherUser, otherBookList).stream()
			.map(UserbookResponse::of)
			.toList();

		// when
		Page<UserbookResponse> result = userbookService.findMyExchangableUserbookPage(Pageable.ofSize(10));

		// then
		List<UserbookResponse> content = result.getContent();
		assertThat(content).isNotEmpty().allMatch(myUserbookList::contains).noneMatch(otherUserbookList::contains);
	}

	private List<User> makeRandomUserList(int size) {
		return Stream.generate(() -> User.builder().build()).limit(size).peek(userRepository::save).toList();
	}

	private List<Book> makeRandomBookList(int size) {
		return Stream.generate(() -> Book.builder().isbn(RandomString.make()).build())
			.limit(size)
			.peek(bookRepository::save)
			.toList();
	}

	private List<Userbook> makeUserbookList(User user, List<Book> bookList) {
		return bookList.stream()
			.map(book -> Userbook.builder()
				.user(user)
				.book(book)
				.registerType(RegisterType.RENTAL_EXCHANGE)
				.tradeStatus(TradeStatus.RENTAL_EXCHANGE_AVAILABLE)
				.build())
			.map(userbookRepository::save)
			.toList();
	}

	private List<Userbook> makeUserbookList(List<User> userList, List<Book> bookList, RegisterType registerType) {
		assertEquals(userList.size(), bookList.size());

		List<Userbook> userbookList = new ArrayList<>();
		for (int i = 0; i < userList.size(); i++) {
			Userbook userbook = Userbook.builder()
				.user(userList.get(i))
				.book(bookList.get(i))
				.registerType(registerType)
				.tradeStatus(registerType.getDefaultTradeStatus())
				.qualityStatus(QualityStatus.GOOD)
				.build();
			userbookList.add(userbook);
		}
		userbookRepository.saveAll(userbookList);

		return userbookList;
	}

	private void makeRental(List<User> borrowerList, Userbook userbook) {
		borrowerList.stream()
			.map(user -> Rental.builder().user(user).userbook(userbook).rentalStatus(RentalStatus.IN_PROGRESS).build())
			.forEach(rentalRepository::save);
	}

	private void makeExchange(List<Userbook> senderUserbookList, Userbook receiverUserbook) {
		senderUserbookList.stream()
			.map(userbook -> Exchange.builder().senderBook(userbook).receiverBook(receiverUserbook).build())
			.forEach(exchangeRepository::save);
	}
}