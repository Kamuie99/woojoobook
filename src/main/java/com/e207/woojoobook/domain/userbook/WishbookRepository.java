package com.e207.woojoobook.domain.userbook;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.e207.woojoobook.domain.user.User;

public interface WishbookRepository extends JpaRepository<Wishbook, Long>, WishbookFindRepository {
	@EntityGraph(attributePaths = "user")
	List<Wishbook> findWithUserByUser(User user);

	@EntityGraph(attributePaths = "userbook")
	Optional<Wishbook> findWithUserbookByUserAndUserbookId(User user, Long userbookId);
}
