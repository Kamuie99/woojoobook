package com.e207.woojoobook.api.userbook;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.e207.woojoobook.api.book.response.BookResponse;
import com.e207.woojoobook.api.controller.userbook.request.UserbookCreateRequest;
import com.e207.woojoobook.api.userbook.request.UserbookPageFindRequest;
import com.e207.woojoobook.api.userbook.response.UserbookResponse;
import com.e207.woojoobook.domain.userbook.Userbook;
import com.e207.woojoobook.client.BookSearchClient;
import com.e207.woojoobook.domain.book.Book;
import com.e207.woojoobook.domain.book.BookRepository;
import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.user.UserRepository;
import com.e207.woojoobook.domain.userbook.UserbookFindCondition;
import com.e207.woojoobook.domain.userbook.UserbookRepository;

import jakarta.persistence.EntityManager;

@Service
public class UserbookService {

	private final Integer MAX_AREA_CODE_SIZE;

	private final UserRepository userRepository;
	private final UserbookRepository userbookRepository;
	private final BookRepository bookRepository;
	private final EntityManager em;

	private final BookSearchClient bookSearchClient;

	public UserbookService(@Value("${userbook.search.ereacode.count}") Integer MAX_AREA_CODE_SIZE,
		UserRepository userRepository,
		UserbookRepository userbookRepository, BookRepository bookRepository, EntityManager entityManager,
		BookSearchClient bookSearchClient) {
		this.MAX_AREA_CODE_SIZE = MAX_AREA_CODE_SIZE;
		this.userRepository = userRepository;
		this.userbookRepository = userbookRepository;
		this.bookRepository = bookRepository;
		this.em = entityManager;
		this.bookSearchClient = bookSearchClient;
	}

	@Transactional(readOnly = true)
	public Page<UserbookResponse> findUserbookPageList(Long userId, UserbookPageFindRequest request,
		Pageable pageable) {
		// TODO: 예외 처리
		if (request.areaCodeList().size() > MAX_AREA_CODE_SIZE) {
			throw new RuntimeException("지역 선택이 초과 했을 때 던지는 에러");
		}

		if (request.areaCodeList().isEmpty()) {
			User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("사용자가 존재하지 않을 때 던지는 에러"));
			request.areaCodeList().add(user.getAreaCode());
		}

		return this.userbookRepository.findUserbookList(UserbookFindCondition.of(request), pageable)
			.map(UserbookResponse::of);
	}

	@Transactional
	public UserbookResponse createUserbook(Long userId, UserbookCreateRequest request) {
		User user = em.getReference(User.class, userId);
		if (!userRepository.exists(Example.of(user))) {
			// todo 예외 처리
			throw new RuntimeException("사용자가 존재하지 않을 때 던지는 예외");
		}
		Book book = bookRepository.findById(request.isbn()).orElseGet(() -> processBookNotExist(request.isbn()));

		Userbook userbook = Userbook.builder()
			.book(book)
			.user(user)
			.qualityStatus(request.quality())
			.registerType(request.registerType())
			.tradeStatus(request.registerType().getDefaultTradeStatus())
			.build();
		userbookRepository.save(userbook);

		return UserbookResponse.of(userbook);
	}

	private Book processBookNotExist(String isbn) {
		return bookSearchClient.findBookByIsbn(isbn)
			.map(BookResponse::toEntity)
			.map(bookRepository::save)
			.orElseThrow(() -> new RuntimeException("검색한 책이 존재하지 않을 때 던지는 예외"));    // todo 예외 처리
	}

	public Userbook findDomain(Long id) {
		return userbookRepository.findFetchById(id).orElseThrow(() -> new RuntimeException("userbook not found"));
	}
}
