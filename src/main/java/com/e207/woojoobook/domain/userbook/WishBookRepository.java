package com.e207.woojoobook.domain.userbook;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.e207.woojoobook.domain.user.User;

public interface WishBookRepository extends JpaRepository<WishBook, Long> {
	@EntityGraph(attributePaths = "user")
	List<WishBook> findWithUserByUser(User user);

	@EntityGraph(attributePaths = "userbook")
	Optional<WishBook> findWithUserbookByUserIdAndUserbookId(Long userId, Long userbookId);
}
