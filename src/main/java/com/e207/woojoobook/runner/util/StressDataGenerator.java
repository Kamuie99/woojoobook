package com.e207.woojoobook.runner.util;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.e207.woojoobook.api.book.BookService;
import com.e207.woojoobook.api.book.request.BookFindRequest;
import com.e207.woojoobook.api.book.response.BookListResponse;
import com.e207.woojoobook.api.book.response.BookResponse;
import com.e207.woojoobook.domain.book.Book;
import com.e207.woojoobook.domain.book.BookRepository;
import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.user.UserRepository;
import com.e207.woojoobook.domain.user.point.Point;
import com.e207.woojoobook.domain.user.point.PointHistory;
import com.e207.woojoobook.domain.user.point.PointRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 부하 테스트용 데이터를 생성하는 클래스
 */
@RequiredArgsConstructor
@Slf4j
@Profile("stress-data")
@Component
public class StressDataGenerator {

	private final BookService bookService;
	private final BookRepository bookRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final PointRepository pointRepository;

	@Transactional
	public void generateData() throws IOException {
		generateBookData();
		generateUserData(100);
	}

	private void generateBookData() throws IOException {
		log.info("Generating book data...");
		File file = new ClassPathResource("json/randomWord.json").getFile();
		ObjectMapper objectMapper = new ObjectMapper();
		Set<String> keywords = objectMapper.readValue(file, Set.class);

		List<Book> bookList = new ArrayList<>();
		for (String keyword : keywords) {
			log.info(keyword);
			BookFindRequest request = BookFindRequest.builder().keyword(keyword).page(1).build();
			BookListResponse response = bookService.findBookList(request);
			response.bookList().stream().map(BookResponse::toEntity).forEach(bookList::add);
		}
		bookRepository.saveAll(bookList);
	}

	private void generateUserData(int n) {
		log.info("Generate user data...");
		String password = passwordEncoder.encode("1234");
		String areaCode = "2644056000";    // 녹산동
		List<User> userList = new ArrayList<>();
		List<Point> pointList = new ArrayList<>();

		for (int i = 0; i < n; i++) {
			String email = String.format("test%d@test.com", i);
			String nickname = String.format("test%d", i);
			User user = User.builder()
				.email(email)
				.nickname(nickname)
				.password(password)
				.areaCode(areaCode)
				.lastLoginDate(LocalDate.now())
				.build();
			userList.add(user);

			Point point = Point.builder()
				.user(user)
				.history(PointHistory.ATTENDANCE)
				.amount(100_000_000)
				.build();
			pointList.add(point);
		}
		userRepository.saveAll(userList);
		pointRepository.saveAll(pointList);
	}
}
