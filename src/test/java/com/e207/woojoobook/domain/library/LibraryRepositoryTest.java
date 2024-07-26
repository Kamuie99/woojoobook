package com.e207.woojoobook.domain.library;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.global.util.DynamicQueryHelper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@DataJpaTest
@Import(DynamicQueryHelper.class)
class LibraryRepositoryTest {

	@Autowired
	private LibraryRepository libraryRepository;

	@PersistenceContext
	private EntityManager em;

	@DisplayName("나의 서재에 있는 카테고리 중 orderNumber의 최대값을 조회한다")
	@Test
	void findMaxOrderNumber() {
		// given
		User testUser = mock(User.class);
		em.persist(testUser);

		Library library1 = new Library(testUser, "카테고리1", "책 리스트1", 2L);
		Library library2 = new Library(testUser, "카테고리2", "책 리스트2", 4L);
		em.persist(library1);
		em.persist(library2);

		em.flush();

		// when
		Optional<Long> result = libraryRepository.findMaxOrderNumber();

		// then
		assertThat(result).isPresent();
		assertThat(result).hasValue(4L);
	}

	@DisplayName("나의 서재에 카테고리가 없으면 빈 Optional을 반환한다")
	@Test
	void findMaxOrderNumberWhenCategoryEmpty() {
		// given
		User testUser = mock(User.class);
		em.persist(testUser);
		em.flush();

		// when
		Optional<Long> result = libraryRepository.findMaxOrderNumber();

		// then
		assertThat(result).isEmpty();
	}
}