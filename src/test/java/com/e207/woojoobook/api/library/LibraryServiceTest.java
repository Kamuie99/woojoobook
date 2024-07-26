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

		Library library = new Library(user, "카테고리1", "책 리스트", 1L);
		libraryRepository.save(library);

		// when
		LibraryResponse libraryResponse = libraryService.find(library.getId());

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
		assertThatThrownBy(() -> libraryService.find(1L))
			.isInstanceOf(RuntimeException.class)
			.hasMessage("library not found");
	}

	@DisplayName("새로운 카테고리를 생성한다")
	@Test
	void createNewCategory() {
		// given
		User user = User.builder().build();
		userRepository.save(user);
		when(userHelper.findCurrentUser()).thenReturn(user);

		LibraryCreateRequest libraryCreateRequest = new LibraryCreateRequest("카테고리명", "책 리스트");

		// when
		LibraryResponse result = libraryService.create(libraryCreateRequest);

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

		Library library = new Library(user, "카테고리1", "책 리스트", 1L);
		libraryRepository.save(library);

		LibraryUpdateRequest libraryUpdateRequest = new LibraryUpdateRequest("카테고리명 수정", "책 리스트 수정");

		// when
		LibraryResponse result = libraryService.update(library.getId(), libraryUpdateRequest);

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

		LibraryUpdateRequest libraryUpdateRequest = new LibraryUpdateRequest("카테고리명 수정", "책 리스트 수정");

		// when & then
		assertThatThrownBy(() -> libraryService.update(1L, libraryUpdateRequest))
			.isInstanceOf(RuntimeException.class)
			.hasMessage("library not found");
	}

	@DisplayName("등록된 카테고리를 삭제한다")
	@Test
	void delete() {
		// given
		User user = User.builder().build();
		userRepository.save(user);
		when(userHelper.findCurrentUser()).thenReturn(user);

		Library library = new Library(user, "카테고리1", "책 리스트", 1L);
		libraryRepository.save(library);

		// when
		libraryService.delete(library.getId());

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
		assertThatThrownBy(() -> libraryService.delete(1L))
			.isInstanceOf(RuntimeException.class)
			.hasMessage("library not found");
	}

	@DisplayName("카테고리의 순서를 교환한다")
	@Test
	void swapOrderNumberNumber() {
		// given
		User user = User.builder().build();
		userRepository.save(user);
		when(userHelper.findCurrentUser()).thenReturn(user);

		Library library1 = new Library(user, "카테고리1", "책 리스트1", 1L);
		Library library2 = new Library(user, "카테고리2", "책 리스트2", 3L);
		libraryRepository.save(library1);
		libraryRepository.save(library2);

		// when
		libraryService.swapOrderNumber(library1.getId(), library2.getId());

		// then
		Library updatedLibrary1 = libraryRepository.findById(library1.getId()).orElseThrow();
		Library updatedLibrary2 = libraryRepository.findById(library2.getId()).orElseThrow();

		assertThat(updatedLibrary1.getOrderNumber()).isEqualTo(3L);
		assertThat(updatedLibrary2.getOrderNumber()).isEqualTo(1L);
	}
}