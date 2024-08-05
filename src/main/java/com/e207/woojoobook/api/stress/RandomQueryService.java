package com.e207.woojoobook.api.stress;

import static com.e207.woojoobook.global.exception.ErrorCode.*;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.e207.woojoobook.api.book.response.BookResponse;
import com.e207.woojoobook.api.exchange.response.ExchangeResponse;
import com.e207.woojoobook.api.extension.ExtensionResponse;
import com.e207.woojoobook.api.rental.response.RentalResponse;
import com.e207.woojoobook.api.stress.repository.RandomBookRepository;
import com.e207.woojoobook.api.stress.repository.RandomExchangeOfferRepository;
import com.e207.woojoobook.api.stress.repository.RandomExtensionOfferRepository;
import com.e207.woojoobook.api.stress.repository.RandomRentalOfferRepository;
import com.e207.woojoobook.api.stress.repository.RandomUserbookRepository;
import com.e207.woojoobook.api.userbook.response.UserbookResponse;
import com.e207.woojoobook.domain.exchange.Exchange;
import com.e207.woojoobook.domain.extension.Extension;
import com.e207.woojoobook.domain.rental.Rental;
import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.userbook.TradeStatus;
import com.e207.woojoobook.domain.userbook.Userbook;
import com.e207.woojoobook.global.exception.ErrorException;
import com.e207.woojoobook.global.helper.UserHelper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Profile("stress")
@Service
public class RandomQueryService {

	private final RandomBookRepository randomBookRepository;
	private final RandomUserbookRepository randomUserbookRepository;
	private final RandomRentalOfferRepository randomRentalOfferRepository;
	private final RandomExchangeOfferRepository randomExchangeOfferRepository;
	private final RandomExtensionOfferRepository randomExtensionOfferRepository;
	private final UserHelper userHelper;

	@Transactional(readOnly = true)
	public BookResponse findBook() {
		return BookResponse.of(randomBookRepository.findRandomBook());
	}

	@Transactional(readOnly = true)
	public Userbook findTradeAvailableUserbook(TradeStatus tradeStatus) {
		User user = userHelper.findCurrentUser();

		return switch (tradeStatus) {
			case RENTAL_AVAILABLE -> findRentalAvailableUserbook(user);
			case EXCHANGE_AVAILABLE -> findExchangeAvailableUserbook(user);
			default -> throw new ErrorException(BadRequest);
		};
	}

	@Transactional(readOnly = true)
	public Userbook findBorrowedUserbook() {
		User user = userHelper.findCurrentUser();
		return randomUserbookRepository.findBorrowed(user.getId())
			.orElseThrow(() -> new ErrorException(NotFound));
	}

	@Transactional(readOnly = true)
	public Userbook findRentedUserbook() {
		User user = userHelper.findCurrentUser();
		return randomUserbookRepository.findRented(user.getId())
			.orElseThrow(() -> new ErrorException(NotFound));
	}

	@Transactional(readOnly = true)
	public Userbook findMyExchangeableUserbook() {
		User user = userHelper.findCurrentUser();
		return randomUserbookRepository.findMyExchangeable(user.getId())
			.orElseThrow(() -> new ErrorException(NotFound));
	}

	@Transactional(readOnly = true)
	public Rental findReceivedRentalOffer() {
		User user = userHelper.findCurrentUser();
		return randomRentalOfferRepository.findReceivedRentalOffer(user.getId())
			.orElseThrow(() -> new ErrorException(NotFound));
	}

	@Transactional(readOnly = true)
	public Exchange findReceivedExchangeOffer() {
		User user = userHelper.findCurrentUser();
		return randomExchangeOfferRepository.findReceivedExchangeOffer(user.getId())
			.orElseThrow(() -> new ErrorException(NotFound));
	}

	@Transactional(readOnly = true)
	public Extension findReceivedExtensionOffer() {
		User user = userHelper.findCurrentUser();
		return randomExtensionOfferRepository.findReceivedExtensionOffer(user.getId())
			.orElseThrow(() -> new ErrorException(NotFound));
	}

	private Userbook findRentalAvailableUserbook(User user) {
		return randomUserbookRepository.findRentalOfferAvailable(user.getId())
			.orElseThrow(() -> new ErrorException(NotFound));
	}

	private Userbook findExchangeAvailableUserbook(User user) {
		return randomUserbookRepository.findExchangeOfferAvailable(user.getId())
			.orElseThrow(() -> new ErrorException(NotFound));
	}
}
