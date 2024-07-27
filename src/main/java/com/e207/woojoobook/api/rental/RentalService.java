package com.e207.woojoobook.api.rental;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.e207.woojoobook.api.rental.event.RentalOfferEvent;
import com.e207.woojoobook.api.rental.request.RentalOfferRespondRequest;
import com.e207.woojoobook.api.rental.response.RentalOfferResponse;
import com.e207.woojoobook.api.user.UserPersonalFacade;
import com.e207.woojoobook.api.user.event.PointEvent;
import com.e207.woojoobook.domain.rental.Rental;
import com.e207.woojoobook.domain.rental.RentalRepository;
import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.user.point.PointHistory;
import com.e207.woojoobook.domain.userbook.TradeStatus;
import com.e207.woojoobook.domain.userbook.Userbook;
import com.e207.woojoobook.domain.userbook.UserbookRepository;
import com.e207.woojoobook.domain.userbook.event.UserBookTradeStatusUpdateEvent;
import com.e207.woojoobook.global.helper.UserHelper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RentalService {

	private final UserbookRepository userbookRepository;
	private final RentalRepository rentalRepository;
	private final UserPersonalFacade userPersonalFacade;
	private final ApplicationEventPublisher eventPublisher;
	private final UserHelper userHelper;

	@Transactional
	public RentalOfferResponse rentalOffer(Long userbooksId) {
		Userbook userbook = validateAndFindUserbook(userbooksId);
		User currentUser = validateAndFindCurrentUser();

		checkIsNotOwner(userbook, currentUser);

		Rental rental = Rental.builder()
			.user(currentUser)
			.userbook(userbook)
			.build();
		Rental savedRental = rentalRepository.save(rental);

		return new RentalOfferResponse(savedRental.getId());
	}

	@Transactional
	public void offerRespond(Long offerId, RentalOfferRespondRequest request) {
		Rental rental = validateAndFindRental(offerId);
		User currentUser = validateAndFindCurrentUser();

		checkCurrentUserIsOwner(rental.getUserbook().getUser(), currentUser);

		boolean isApproved = request.isApproved();
		boolean hasSufficientPoints = userPersonalFacade.checkPointToRental(rental.getUser().getId());

		if (!isApproved || !hasSufficientPoints) {
			handleRentalResponse(rental, isApproved);
			return;
		}

		approveRentalRequest(rental, request);
	}

	@Transactional
	public void deleteRentalOffer(Long offerId) {
		Rental rental = validateAndFindRental(offerId);
		User currentUser = validateAndFindCurrentUser();

		checkCurrentUserIsOwner(rental.getUser(), currentUser);
		rentalRepository.delete(rental);
	}

	@Transactional
	public void giveBack(Long rentalId) {
		Rental rental = validateAndFindRental(rentalId);
		User currentUser = validateAndFindCurrentUser();

		checkCurrentUserIsOwner(rental.getUserbook().getUser(), currentUser);
		rental.giveBack();

		Userbook userbook = rental.getUserbook();
		TradeStatus tradeStatus = userbook.getRegisterType().getDefaultTradeStatus();
		userbook.updateTradeStatus(tradeStatus);

		eventPublisher.publishEvent(new UserBookTradeStatusUpdateEvent(userbook, tradeStatus));
	}

	private User validateAndFindCurrentUser() {
		User currentUser = userHelper.findCurrentUser();
		if (!userPersonalFacade.checkPointToRental(currentUser.getId())) {
			throw new RuntimeException("대여에 필요한 포인트가 부족합니다.");
		}
		return currentUser;
	}

	private Rental validateAndFindRental(Long rentalId) {
		return rentalRepository.findById(rentalId)
			.orElseThrow(() -> new RuntimeException("존재하지 않는 대여 신청입니다"));
	}

	private Userbook validateAndFindUserbook(Long userbooksId) {
		Userbook userbook = userbookRepository.findById(userbooksId)
			.orElseThrow(() -> new RuntimeException("존재하지 않는 도서입니다."));
		if (!userbook.isAvailable()) {
			throw new RuntimeException("접근이 불가능한 도서 상태입니다.");
		}
		return userbook;
	}

	private void checkCurrentUserIsOwner(User owner, User currentUser) {
		if (!owner.getId().equals(currentUser.getId())) {
			throw new RuntimeException("권한이 없습니다.");
		}
	}

	private void checkIsNotOwner(Userbook userbook, User currentUser) {
		if (userbook.getUser().equals(currentUser)) {
			throw new RuntimeException("자신의 책은 대여 신청 대상이 아닙니다.");
		}
	}

	private void updateUserbookIfApproved(RentalOfferRespondRequest request, Rental rental) {
		if (request.isApproved()) {
			Userbook userbook = userbookRepository.findWithWishBookById(rental.getUserbook().getId());
			if (!userbook.isAvailable()) {
				throw new RuntimeException("대여가 불가능한 도서 상태입니다.");
			}
			userbook.updateTradeStatus(TradeStatus.RENTED);
		}
	}

	private void handleRentalResponse(Rental rental, boolean isApproved) {
		rental.respond(isApproved);
		eventPublisher.publishEvent(new RentalOfferEvent(rental, isApproved));
	}

	private void approveRentalRequest(Rental rental, RentalOfferRespondRequest request) {
		rental.respond(request.isApproved());
		updateUserbookIfApproved(request, rental);
		publishRentalApprovalEvents(rental);
	}

	private void publishRentalApprovalEvents(Rental rental) {
		eventPublisher.publishEvent(new RentalOfferEvent(rental, true));
		eventPublisher.publishEvent(new PointEvent(rental.getUser(), PointHistory.USE_BOOK_RENTAL));
		eventPublisher.publishEvent(new UserBookTradeStatusUpdateEvent(rental.getUserbook(), TradeStatus.RENTED));
	}
}
