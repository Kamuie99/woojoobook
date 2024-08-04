package com.e207.woojoobook.api.userbook;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import net.bytebuddy.utility.RandomString;

import com.e207.woojoobook.api.book.response.BookResponse;
import com.e207.woojoobook.api.user.response.UserResponse;
import com.e207.woojoobook.api.userbook.response.UserbookResponse;
import com.e207.woojoobook.domain.book.Book;
import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.userbook.QualityStatus;
import com.e207.woojoobook.domain.userbook.RegisterType;
import com.e207.woojoobook.domain.userbook.TradeStatus;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(MyUserbookController.class)
class MyUserbookControllerTest {

	@Autowired
	MockMvc mvc;
	@Autowired
	ObjectMapper objectMapper;
	@MockBean
	UserbookService userbookService;

	@WithMockUser
	@DisplayName("사용자가 좋아요 표시한 도서를 페이지로 조회한다.")
	@Test
	void When_FindLikedUserbook_Expect_ReturnUserbookPage() throws Exception {
		// given
		UserResponse otherUser = createUser(2L);
		List<BookResponse> bookList = List.of(createBook(), createBook());
		List<UserbookResponse> otherUserbookList = createUserbookList(bookList, otherUser);
		Page<UserbookResponse> expectResponse = new PageImpl<>(otherUserbookList);
		String expectResponseJson = objectMapper.writeValueAsString(expectResponse);

		given(userbookService.findLikedUserbookPageList(any(Pageable.class))).willReturn(expectResponse);

		// when
		ResultActions action = mvc.perform(get("/users/userbooks/likes"));

		// then
		action.andExpect(status().isOk());
		action.andExpect(content().json(expectResponseJson));
	}

	@WithMockUser
	@DisplayName("사용자가 등록한 도서를 페이지로 조회한다.")
	@Test
	void When_FindOwnedUserbook_Expect_ReturnUserbookPage() throws Exception {
		// given
		UserResponse currentUser = createUser(1L);
		List<BookResponse> bookList = Stream.generate(this::createBook).limit(3).toList();
		List<UserbookResponse> currentUserbookList = createUserbookList(bookList, currentUser);
		PageImpl<UserbookResponse> expectedResponse = new PageImpl<>(currentUserbookList);
		String expectedResponseJson = objectMapper.writeValueAsString(expectedResponse);

		given(userbookService.findOwnedUserbookPage(any(Pageable.class))).willReturn(expectedResponse);

		// when
		ResultActions action = mvc.perform(get("/users/userbooks/registered"));

		// then
		action.andExpect(status().isOk());
		action.andExpect(content().json(expectedResponseJson));
	}

	private UserResponse createUser(Long id) {
		User user = User.builder().id(id).build();
		return UserResponse.of(user);
	}

	private BookResponse createBook() {
		Book book = Book.builder().isbn(RandomString.make()).build();
		return BookResponse.of(book);
	}

	private List<UserbookResponse> createUserbookList(List<BookResponse> bookList, UserResponse user) {
		return Stream.iterate(0, (id) -> id + 1)
			.limit(bookList.size() - 1)
			.map((id) -> createUserbookResponse(id.longValue(), user, bookList.get(id)))
			.toList();
	}

	private UserbookResponse createUserbookResponse(Long id, UserResponse owner, BookResponse book) {
		return UserbookResponse.builder()
			.id(id)
			.ownerInfo(owner)
			.bookInfo(book)
			.registerType(RegisterType.RENTAL_EXCHANGE)
			.tradeStatus(TradeStatus.RENTAL_EXCHANGE_AVAILABLE)
			.qualityStatus(QualityStatus.VERY_GOOD)
			.areaCode(RandomString.make(10))
			.build();
	}
}