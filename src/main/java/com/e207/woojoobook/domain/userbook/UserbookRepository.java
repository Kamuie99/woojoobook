package com.e207.woojoobook.domain.userbook;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserbookRepository extends JpaRepository<Userbook, Long>, UserbookFindRepository {
	@EntityGraph(attributePaths = "wishBooks")
	Userbook findWithWishBookById(Long id);

	@Query("select ub from Userbook ub"
		+ " join fetch Book sb on ub.book.isbn = sb.isbn"
		+ " join fetch Book rb on ub.book.isbn = rb.isbn"
		+ " where ub.id = :id")
	Optional<Userbook> findFetchById(Long id);

}
