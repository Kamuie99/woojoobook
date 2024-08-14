package com.e207.woojoobook.api.rental;

import static com.e207.woojoobook.domain.rental.RentalStatus.*;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.e207.woojoobook.api.rental.event.RentalOfferEvent;
import com.e207.woojoobook.api.rental.request.RentalFindCondition;
import com.e207.woojoobook.api.rental.request.RentalOfferRespondRequest;
import com.e207.woojoobook.api.rental.response.RentalOfferResponse;
import com.e207.woojoobook.api.rental.response.RentalResponse;
import com.e207.woojoobook.api.user.UserPersonalFacade;
import com.e207.woojoobook.domain.rental.Rental;
import com.e207.woojoobook.domain.rental.RentalRepository;
import com.e207.woojoobook.domain.rental.RentalStatus;
import com.e207.woojoobook.domain.rental.RentalUserCondition;
import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.userbook.TradeStatus;
import com.e207.woojoobook.domain.userbook.Userbook;
import com.e207.woojoobook.domain.userbook.UserbookRepository;
import com.e207.woojoobook.domain.userbook.event.UserBookTradeStatusUpdateEvent;
import com.e207.woojoobook.global.exception.ErrorCode;
import com.e207.woojoobook.global.exception.ErrorException;
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
		User currentUser = validateCanRentalAndFindCurrentUser();
		checkIsNotDuplicated(currentUser, userbook, OFFERING);

		Rental rental = userbook.createRental(currentUser);

		Rental savedRental = this.rentalRepository.save(rental);

		return new RentalOfferResponse(savedRental.getId());
	}

	@Transactional
	public Page<RentalResponse> findByCondition(RentalFindCondition conditionForFind, Pageable pageable) {
		Long userId = userHelper.findCurrentUser().getId();
		RentalUserCondition userCondition = conditionForFind.userCondition();
		RentalStatus rentalStatus = conditionForFind.rentalStatus();
		return rentalRepository.findByStatusAndUserCondition(userId, rentalStatus, userCondition, pageable)
			.map(RentalResponse::of);
	}

	@Transactional
	public void offerRespond(Long offerId, RentalOfferRespondRequest request) {
		Rental rental = validateAndFindRental(offerId);
		User currentUser = this.userHelper.findCurrentUser();

		if (rental.isSameOwner(currentUser.getId())) {
			throw new ErrorException(ErrorCode.ForbiddenError);
		}

		boolean isApproved = request.isApproved();
		boolean hasSufficientPoints = this.userPersonalFacade.checkPointToRental(rental.getUser().getId());

		if (!isApproved || !hasSufficientPoints) {
			handleRentalResponse(rental, false);
			return;
		}

		approveRentalRequest(rental, request);
		this.rentalRepository.save(rental);
	}

	@Transactional
	public void deleteRentalOffer(Long offerId) {
		Rental rental = validateAndFindRental(offerId);
		User currentUser = this.userHelper.findCurrentUser();

		checkCurrentUserIsOwner(rental.getUser(), currentUser);
		this.rentalRepository.delete(rental);
	}

	@Transactional
	public void giveBack(Long rentalId) {
		Rental rental = validateAndFindRental(rentalId);
		User currentUser = this.userHelper.findCurrentUser();

		checkCurrentUserIsOwner(rental.getUserbook().getUser(), currentUser);
		if (rental.getRentalStatus() != IN_PROGRESS) {
			throw new ErrorException(ErrorCode.InvalidAccess);
		}
		rental.giveBack();

		Userbook userbook = rental.getUserbook();
		resetUserbookTradeStatus(userbook);
	}

	@Transactional
	public void giveBackByUserbookId(Long userbookId) {
		User currentUser = this.userHelper.findCurrentUser();
		Userbook userbook = this.userbookRepository.findById(userbookId)
			.orElseThrow(() -> new ErrorException(ErrorCode.NotFound));
		checkCurrentUserIsOwner(userbook.getUser(), currentUser);

		Rental rental = validateAndFindRentalInProgress(userbook);
		rental.giveBack();

		resetUserbookTradeStatus(userbook);
	}

	private void resetUserbookTradeStatus(Userbook userbook) {
		TradeStatus tradeStatus = userbook.getRegisterType().getDefaultTradeStatus();
		userbook.updateTradeStatus(tradeStatus);

		this.eventPublisher.publishEvent(new UserBookTradeStatusUpdateEvent(userbook, tradeStatus));
	}

	private User validateCanRentalAndFindCurrentUser() {
		User currentUser = this.userHelper.findCurrentUser();
		if (!this.userPersonalFacade.checkPointToRental(currentUser.getId())) {
			throw new ErrorException(ErrorCode.NotEnoughPoint);
		}

		return currentUser;
	}

	private Rental validateAndFindRental(Long rentalId) {
		return this.rentalRepository.findWithUserbookById(rentalId).orElseThrow(() -> new ErrorException(ErrorCode.NotFound));
	}

	private Rental validateAndFindRentalInProgress(Userbook userbook) {
		List<Rental> rentalList = this.rentalRepository.findAllByUserbookAndRentalStatus(userbook, IN_PROGRESS);

		if (rentalList.isEmpty()) {
			throw new ErrorException(ErrorCode.NotFound);
		}
		if (rentalList.size() > 1) {
			throw new ErrorException(ErrorCode.DuplicateRented);
		}

		return rentalList.getFirst();
	}

	private Userbook validateAndFindUserbook(Long userbooksId) {
		return this.userbookRepository.findWithUserById(userbooksId)
			.orElseThrow(() -> new ErrorException(ErrorCode.NotFound));
	}

	private void checkCurrentUserIsOwner(User owner, User currentUser) {
		if (!owner.getId().equals(currentUser.getId())) {
			throw new ErrorException(ErrorCode.ForbiddenError);
		}
	}

	private void handleRentalResponse(Rental rental, boolean isApproved) {
		rental.respond(isApproved);
		this.eventPublisher.publishEvent(new RentalOfferEvent(rental, isApproved));
	}

	private void approveRentalRequest(Rental rental, RentalOfferRespondRequest request) {
		rental.respond(request.isApproved());
		publishRentalApprovalEvents(rental);
		rejectPreviousRentalRequests(rental.getUserbook());
	}

	private void publishRentalApprovalEvents(Rental rental) {
		this.eventPublisher.publishEvent(new RentalOfferEvent(rental, true));
		this.eventPublisher.publishEvent(new UserBookTradeStatusUpdateEvent(rental.getUserbook(), TradeStatus.RENTED));
	}

	private void checkIsNotDuplicated(User currentUser, Userbook userbook, RentalStatus status) {
		boolean existsRentalByRentalStatus = this.rentalRepository.existsRentalByRentalStatus(currentUser.getId(),
			userbook.getId(), status);

		if (existsRentalByRentalStatus) {
			throw new ErrorException(ErrorCode.NotAcceptDuplicate);
		}
	}

	private void rejectPreviousRentalRequests(Userbook userbook) {
		this.rentalRepository.findAllByUserbookAndRentalStatus(userbook, OFFERING).forEach(r -> {
			handleRentalResponse(r, false);
		});
	}
}
