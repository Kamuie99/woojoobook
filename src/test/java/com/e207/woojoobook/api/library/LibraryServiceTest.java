package com.e207.woojoobook.api.library;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.e207.woojoobook.api.library.request.LibraryCreateRequest;
import com.e207.woojoobook.api.library.request.LibraryUpdateRequest;
import com.e207.woojoobook.api.library.response.LibraryResponse;
import com.e207.woojoobook.domain.library.Library;
import com.e207.woojoobook.domain.library.LibraryRepository;
import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.user.UserRepository;
import com.e207.woojoobook.global.helper.UserHelper;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class LibraryServiceTest {

	@Autowired
	private LibraryService libraryService;
	@Autowired
	private LibraryRepository libraryRepository;
	@Autowired
	private UserRepository userRepository;
	@MockBean
	private UserHelper userHelper;

	@DisplayName("사용자와 카테고리의 ID로 카테고리를 조회한다")
	@Test
	void findByIdAndUserId() {
		// given
		User user = User.builder().build();
		userRepository.save(user);
		when(userHelper.findCurrentUser()).thenReturn(user);

		Library library = Library.builder()
			.user(user)
			.name("카테고리")
			.bookList("책 리스트")
			.orderNumber(1L)
			.build();
		libraryRepository.save(library);

		// when
		LibraryResponse libraryResponse = libraryService.find(user.getId(), library.getId());

		// then
		assertThat(libraryResponse).isNotNull();
		assertThat(libraryResponse.id()).isEqualTo(library.getId());
	}

	@DisplayName("존재하지 않는 카테고리 조회 시 에러가 발생한다.")
	@Test
	void findByIdAndUserIdNotFound() {
		// given
		User user = User.builder().build();
		userRepository.save(user);
		when(userHelper.findCurrentUser()).thenReturn(user);

		// when & then
		assertThatThrownBy(() -> libraryService.find(1L, user.getId()))
			.isInstanceOf(RuntimeException.class)
			.hasMessage("Category not found");
	}

	@DisplayName("새로운 카테고리를 생성한다")
	@Test
	void createNewCategory() {
		// given
		User user = User.builder().build();
		userRepository.save(user);
		when(userHelper.findCurrentUser()).thenReturn(user);

		LibraryCreateRequest libraryCreateRequest = LibraryCreateRequest.builder()
			.categoryName("카테고리명")
			.books("책 리스트")
			.build();

		// when
		LibraryResponse result = libraryService.create(user.getId(), libraryCreateRequest);

		// then
		assertThat(result).isNotNull();
		assertThat(result.categoryName()).isEqualTo("카테고리명");
		assertThat(result.books()).isEqualTo("책 리스트");
	}

	@DisplayName("카테고리 정보를 수정한다")
	@Test
	void update() {
		User user = User.builder().build();
		userRepository.save(user);
		when(userHelper.findCurrentUser()).thenReturn(user);

		Library library = Library.builder()
			.user(user)
			.name("카테고리")
			.bookList("책 리스트")
			.orderNumber(1L)
			.build();
		libraryRepository.save(library);

		LibraryUpdateRequest libraryUpdateRequest = LibraryUpdateRequest.builder()
			.categoryName("카테고리명 수정")
			.books("책 리스트 수정")
			.build();

		// when
		LibraryResponse result = libraryService.update(user.getId(), library.getId(), libraryUpdateRequest);

		// then
		assertThat(result.categoryName()).isEqualTo("카테고리명 수정");
		assertThat(result.books()).isEqualTo("책 리스트 수정");
	}

	@DisplayName("등록되지 않은 카테고리 수정 요청 시 에러가 발생한다")
	@Test
	void updateNotFound() {
		// given
		User user = User.builder().build();
		userRepository.save(user);
		when(userHelper.findCurrentUser()).thenReturn(user);

		LibraryUpdateRequest libraryUpdateRequest = LibraryUpdateRequest.builder()
			.categoryName("카테고리명 수정")
			.books("책 리스트 수정")
			.build();

		// when & then
		assertThatThrownBy(() -> libraryService.update(user.getId(), 1L, libraryUpdateRequest))
			.isInstanceOf(RuntimeException.class)
			.hasMessage("Category not found");
	}

	@DisplayName("등록된 카테고리를 삭제한다")
	@Test
	void delete() {
		// given
		User user = User.builder().build();
		userRepository.save(user);
		when(userHelper.findCurrentUser()).thenReturn(user);

		Library library = Library.builder()
			.user(user)
			.name("카테고리")
			.bookList("책 리스트")
			.orderNumber(1L)
			.build();
		libraryRepository.save(library);

		// when
		libraryService.delete(user.getId(), library.getId());

		// then
		assertThat(libraryRepository.findById(library.getId())).isEmpty();
	}

	@DisplayName("등록되지 않은 카테고리 삭제 요청 시 에러가 발생한다")
	@Test
	void deleteNotFound() {
		// given
		User user = User.builder().build();
		userRepository.save(user);
		when(userHelper.findCurrentUser()).thenReturn(user);

		// when & then
		assertThatThrownBy(() -> libraryService.delete(user.getId(), 1L))
			.isInstanceOf(RuntimeException.class)
			.hasMessage("Category not found");
	}

	@DisplayName("카테고리의 순서를 교환한다")
	@Test
	void swapOrderNumberNumber() {
		// given
		User user = User.builder().build();
		userRepository.save(user);
		when(userHelper.findCurrentUser()).thenReturn(user);

		Library library1 = Library.builder()
			.user(user)
			.name("카테고리1")
			.bookList("책 리스트1")
			.orderNumber(1L)
			.build();
		Library library2 = Library.builder()
			.user(user)
			.name("카테고리2")
			.bookList("책 리스트2")
			.orderNumber(3L)
			.build();
		libraryRepository.save(library1);
		libraryRepository.save(library2);

		// when
		libraryService.swapOrderNumber(user.getId(), library1.getId(), library2.getId());

		// then
		Library updatedLibrary1 = libraryRepository.findById(library1.getId()).orElseThrow();
		Library updatedLibrary2 = libraryRepository.findById(library2.getId()).orElseThrow();

		assertThat(updatedLibrary1.getOrderNumber()).isEqualTo(3L);
		assertThat(updatedLibrary2.getOrderNumber()).isEqualTo(1L);
	}
}