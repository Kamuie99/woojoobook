package com.e207.woojoobook.api.userbook.event;

import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.e207.woojoobook.domain.userbook.WishBook;
import com.e207.woojoobook.domain.userbook.WishBookRepository;
import com.e207.woojoobook.domain.rental.Rental;
import com.e207.woojoobook.domain.rental.RentalRepository;
import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.user.UserRepository;
import com.e207.woojoobook.domain.userbook.Userbook;
import com.e207.woojoobook.domain.userbook.UserbookRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class UserEventListener {

	private final UserRepository userRepository;
	private final UserbookRepository userbookRepository;
	private final WishBookRepository wishBookRepository;
	private final RentalRepository rentalRepository;

	@Transactional
	@EventListener
	public void handleUserDeleteEvent(UserDeleteEvent event) {
		User user = event.user();
		List<Userbook> userbooks = this.userbookRepository.findWithUserByUser(user);
		List<WishBook> wishBooks = this.wishBookRepository.findWithUserByUser(user);
		List<Rental> rentals = this.rentalRepository.findWithUserByUser(user);

		userbooks.forEach(Userbook::removeUser);
		wishBooks.forEach(WishBook::removeUser);
		rentals.forEach(Rental::removeUser);
		this.userRepository.delete(user);
	}
}
