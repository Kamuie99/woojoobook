package com.e207.woojoobook.api.userbook;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
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

@ExtendWith(MockitoExtension.class)
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
		List<UserbookResponse> otherUserbookList = createUserbookList(bookList, otherUser,
			RegisterType.RENTAL_EXCHANGE);
		Page<UserbookResponse> expectResponse = new PageImpl<>(otherUserbookList);
		String expectResponseJson = objectMapper.writeValueAsString(expectResponse);

		given(userbookService.findLikedUserbookPageList(any(Pageable.class))).willReturn(expectResponse);

		// when
		ResultActions action = mvc.perform(get("/users/userbooks/likes"));

		// then
		action.andExpect(status().isOk()).andExpect(content().json(expectResponseJson));
	}

	@WithMockUser
	@DisplayName("사용자가 등록한 도서를 페이지로 조회한다.")
	@Test
	void When_FindOwnedUserbook_Expect_ReturnUserbookPage() throws Exception {
		// given
		UserResponse currentUser = createUser(1L);
		List<BookResponse> bookList = Stream.generate(this::createBook).limit(3).toList();
		List<UserbookResponse> currentUserbookList = createUserbookList(bookList, currentUser,
			RegisterType.RENTAL_EXCHANGE);
		PageImpl<UserbookResponse> expectedResponse = new PageImpl<>(currentUserbookList);
		String expectedResponseJson = objectMapper.writeValueAsString(expectedResponse);

		given(userbookService.findMyUserbookPage(isNull(), any(Pageable.class))).willReturn(
			expectedResponse);

		// when
		ResultActions action = mvc.perform(get("/users/userbooks/registered"));

		// then
		action.andExpect(status().isOk()).andExpect(content().json(expectedResponseJson));
	}

	@WithMockUser
	@DisplayName("내 사용자 도서 중 교환 가능한 도서를 페이지로 조회한다.")
	@Test
	void When_FindMyExchangableUserbook_Expect_ReturnExchangableUserbookPage() throws Exception {
		// given
		UserResponse currentUser = createUser(1L);
		List<BookResponse> bookList = Stream.generate(this::createBook).limit(3).toList();
		List<UserbookResponse> currentUserbookList = createUserbookList(bookList, currentUser, RegisterType.EXCHANGE);
		PageImpl<UserbookResponse> expectedResponse = new PageImpl<>(currentUserbookList);
		String expectedResponseJson = objectMapper.writeValueAsString(expectedResponse);

		given(userbookService.findMyExchangableUserbookPage(any(Pageable.class))).willReturn(expectedResponse);

		// when
		ResultActions action = mvc.perform(get("/users/userbooks/exchangable"));

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

	private List<UserbookResponse> createUserbookList(List<BookResponse> bookList, UserResponse user,
		RegisterType registerType) {
		return Stream.iterate(0, (id) -> id + 1)
			.limit(bookList.size() - 1)
			.map((id) -> createUserbookResponse(id.longValue(), user, bookList.get(id), registerType))
			.toList();
	}

	private UserbookResponse createUserbookResponse(Long id, UserResponse owner, BookResponse book,
		RegisterType registerType) {
		return UserbookResponse.builder()
			.id(id)
			.ownerInfo(owner)
			.bookInfo(book)
			.registerType(registerType)
			.tradeStatus(registerType.getDefaultTradeStatus())
			.qualityStatus(QualityStatus.VERY_GOOD)
			.areaCode(RandomString.make(10))
			.build();
	}
}