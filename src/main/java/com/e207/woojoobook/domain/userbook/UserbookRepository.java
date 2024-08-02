package com.e207.woojoobook.domain.userbook;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.e207.woojoobook.domain.user.User;

public interface UserbookRepository extends JpaRepository<Userbook, Long>, UserbookFindRepository {

	@EntityGraph(attributePaths = "user")
	Optional<Userbook> findWithUserById(Long id);

	@Query("select ub from Userbook ub"
		+ " join fetch User u on ub.user.id = u.id"
		+ " join fetch Book b on ub.book.isbn = b.isbn"
		+ " where ub.id = :id")
	Optional<Userbook> findByIdWithUserAndBook(Long id);

	@EntityGraph(attributePaths = "user")
	List<Userbook> findWithUserByUser(User user);

	Optional<Userbook> findUserbookById(Long id);
}
