package com.e207.woojoobook.api.userbook;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import net.bytebuddy.utility.RandomString;

import com.e207.woojoobook.api.book.response.BookResponse;
import com.e207.woojoobook.api.controller.userbook.request.UserbookCreateRequest;
import com.e207.woojoobook.api.userbook.request.UserbookPageFindRequest;
import com.e207.woojoobook.client.BookSearchClient;
import com.e207.woojoobook.domain.book.Book;
import com.e207.woojoobook.domain.book.BookRepository;
import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.user.UserRepository;
import com.e207.woojoobook.domain.userbook.QualityStatus;
import com.e207.woojoobook.domain.userbook.RegisterType;
import com.e207.woojoobook.domain.userbook.UserbookRepository;

import jakarta.transaction.Transactional;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class UserbookServiceTest {

	@MockBean
	private BookSearchClient bookSearchClient;
	@Autowired
	private UserbookService userbookService;
	@Autowired
	private BookRepository bookRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserbookRepository userbookRepository;

	@DisplayName("지역 선택 개수를 초과하면 에러가 발생한다.")
	@Test
	void When_ExceedAreaCode_Expect_ThrowException() {
		// given
		Long userId = 1L;
		Pageable pageable = Pageable.ofSize(10);

		Integer MAX_AREA_CODE_SIZE = 3;
		ReflectionTestUtils.setField(userbookService, "MAX_AREA_CODE_SIZE", MAX_AREA_CODE_SIZE);

		List<String> areaCodeList = List.of("대구", "대전", "부산", "울산");
		UserbookPageFindRequest request = new UserbookPageFindRequest(null, areaCodeList, null);

		// when
		Executable expectException = () -> userbookService.findUserbookPageList(userId, request, pageable);

		// then
		assertThrows(RuntimeException.class, expectException);
	}

	@DisplayName("사용자가 등록하려는 도서가 저장되어 있지 않으면 저장 후 등록한다.")
	@Transactional
	@Test
	void When_NotExistBook_Expect_SaveBook() {
		// given
		User user = User.builder().build();
		userRepository.save(user);

		String expectIsbn = RandomString.make();
		Book book = Book.builder().isbn(expectIsbn).description("test").build();

		BookResponse bookResponse = BookResponse.of(book);
		given(bookSearchClient.findBookByIsbn(any())).willReturn(Optional.of(bookResponse));

		UserbookCreateRequest request = new UserbookCreateRequest(expectIsbn, RegisterType.RENTAL,
			QualityStatus.GOOD);

		// when
		userbookService.createUserbook(user.getId(), request);

		// then
		assertTrue(bookRepository.findById(expectIsbn).isPresent());
	}
}