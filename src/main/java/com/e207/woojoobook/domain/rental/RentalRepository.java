package com.e207.woojoobook.domain.rental;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.userbook.Userbook;

public interface RentalRepository extends JpaRepository<Rental, Long>, RentalRepositoryCustom {
	List<Rental> findAllByUserbook(Userbook userbook);

	@EntityGraph(attributePaths = "user")
	List<Rental> findWithUserByUser(User user);

	@EntityGraph(attributePaths = "userbook")
	Optional<Rental> findWithUserbookById(Long id);

	List<Rental> findAllByUserbookAndRentalStatus(Userbook userbook, RentalStatus rentalStatus);
}
