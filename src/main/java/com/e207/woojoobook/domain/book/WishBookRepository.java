package com.e207.woojoobook.domain.book;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.e207.woojoobook.domain.user.User;

public interface WishBookRepository extends JpaRepository<WishBook, Long> {
	@EntityGraph(attributePaths = "user")
	List<WishBook> findWithUserByUser(User user);
}
