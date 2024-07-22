package com.e207.woojoobook.domain.userbook;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserbookRepository extends JpaRepository<Userbook, Long>, UserbookFindRepository {
	@EntityGraph(attributePaths = "wishBooks")
	Userbook findWithWishBookById(Long id);
}
