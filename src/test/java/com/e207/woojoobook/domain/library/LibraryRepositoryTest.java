package com.e207.woojoobook.domain.library;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.e207.woojoobook.domain.user.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@DataJpaTest
class LibraryRepositoryTest {

	@Autowired
	private LibraryRepository libraryRepository;

	@PersistenceContext
	private EntityManager em;

	@DisplayName("나의 서재에 있는 카테고리 중 orderNumber의 최대값을 조회한다")
	@Test
	void findMaxOrderNumber() {
		// given
		User testUser = User.builder()
			.email("test@email.com")
			.password("password")
			.nickname("test")
			.areaCode("areacode")
			.build();
		em.persist(testUser);

		Library library1 = Library.builder()
			.user(testUser)
			.name("카테고리1")
			.bookList("책 리스트1")
			.orderNumber(2L)
			.build();
		Library library2 = Library.builder()
			.user(testUser)
			.name("카테고리2")
			.bookList("책 리스트2")
			.orderNumber(4L)
			.build();

		em.persist(library1);
		em.persist(library2);

		em.flush();
		em.clear();

		// when
		Optional<Long> result = libraryRepository.findMaxOrderNumber(testUser.getId());

		// then
		assertThat(result).isPresent();
		assertThat(result).hasValue(4L);
	}

	@DisplayName("나의 서재에 카테고리가 없으면 빈 Optional을 반환한다")
	@Test
	void findMaxOrderNumberWhenCategoryEmpty() {
		// given
		User testUser = User.builder()
			.email("test@email.com")
			.password("password")
			.nickname("test")
			.areaCode("areacode")
			.build();
		em.persist(testUser);
		em.flush();

		// when
		Optional<Long> result = libraryRepository.findMaxOrderNumber(testUser.getId());

		// then
		assertThat(result).isEmpty();
	}
}