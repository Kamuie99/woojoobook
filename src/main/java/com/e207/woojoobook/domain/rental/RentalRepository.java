package com.e207.woojoobook.domain.rental;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.e207.woojoobook.domain.userbook.Userbook;
import com.e207.woojoobook.domain.user.User;

public interface RentalRepository extends JpaRepository <Rental, Long> {
	List<Rental> findAllByUserbook(Userbook userbook);
	@EntityGraph(attributePaths = "user")
	List<Rental> findWithUserByUser(User user);
}
