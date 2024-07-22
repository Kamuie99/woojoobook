package com.e207.woojoobook.api.rental;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.e207.woojoobook.api.controller.user.UserBookTradeStatusEvent;
import com.e207.woojoobook.api.rental.response.RentalOfferResponse;
import com.e207.woojoobook.domain.userbook.UserbookRepository;
import com.e207.woojoobook.domain.rental.Rental;
import com.e207.woojoobook.domain.rental.RentalRepository;
import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.userbook.Userbook;
import com.e207.woojoobook.global.helper.UserHelper;
import com.e207.woojoobook.domain.userbook.TradeStatus;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RentalService {

	private final UserbookRepository userbookRepository;
	private final RentalRepository rentalRepository;
	private final ApplicationEventPublisher eventPublisher;
	private final UserHelper userHelper;

	@Transactional
	public RentalOfferResponse rentalOffer(Long userbooksId) {
		Userbook userbook = validateAndFindUserbook(userbooksId);
		User currentUser = this.userHelper.findCurrentUser();

		Rental rental = Rental.builder()
			.user(currentUser)
			.userbook(userbook)
			.build();
		Rental save = this.rentalRepository.save(rental);

		return new RentalOfferResponse(save.getId());
	}

	@Transactional
	public void offerRespond(Long offerId, RentalOfferRespondRequest request) {
		Rental rental = validateAndFindRental(offerId);

		Userbook userbook = this.userbookRepository.findWithWishBookById(rental.getUserbook().getId());
		if (!userbook.isAvailable()) {
			throw new RuntimeException("대여가 불가능한 도서 상태입니다.");
		}
		rental.respond(request.isApproved());

		validateAndUpdateUserbook(request, rental);

		eventPublisher.publishEvent(new RentalOfferEvent(rental, request.isApproved()));
	}

	// TODO : 컨트롤러 어드바이스에서 예외 발생시 로그 남기기
	@Transactional
	public void deleteRentalOffer(Long offerId) {
		this.rentalRepository.deleteById(offerId);
	}

	@Transactional
	public void giveBack(Long rentalId) {
		Rental rental = validateAndFindRental(rentalId);

		rental.giveBack();
		Userbook userbook = rental.getUserbook();
		TradeStatus tradeStatus = userbook.getRegisterType().getDefaultTradeStatus();
		userbook.updateTradeStatus(tradeStatus);

		eventPublisher.publishEvent(new UserBookTradeStatusEvent(userbook, tradeStatus));
	}

	private Rental validateAndFindRental(Long rentalId) {
		Rental rental = this.rentalRepository.findById(rentalId)
			.orElseThrow(() -> new RuntimeException("존재하지 않는 대여 신청입니다"));
		return rental;
	}

	private void validateAndUpdateUserbook(RentalOfferRespondRequest request, Rental rental) {
		if (request.isApproved()) {
			Userbook userbook = this.userbookRepository.findWithWishBookById(rental.getUserbook().getId());
			if (!userbook.isAvailable()) {
				throw new RuntimeException("대여가 불가능한 도서 상태입니다.");
			}
			userbook.updateTradeStatus(TradeStatus.RENTED);
		}
	}

	private Userbook validateAndFindUserbook(Long userbooksId) {
		Userbook userbook = this.userbookRepository.findById(userbooksId)
			.orElseThrow(() -> new RuntimeException("존재하지 않는 도서입니다."));

		if (!userbook.isAvailable()) {
			throw new RuntimeException("접근이 불가능한 도서 상태입니다.");
		}

		return userbook;
	}
}
