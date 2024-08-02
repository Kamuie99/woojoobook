package com.e207.woojoobook.api.extension;

import static com.e207.woojoobook.domain.extension.ExtensionStatus.*;

import java.time.LocalDateTime;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.e207.woojoobook.api.extension.event.ExtensionEvent;
import com.e207.woojoobook.api.extension.event.ExtensionResultEvent;
import com.e207.woojoobook.api.extension.request.ExtensionFindCondition;
import com.e207.woojoobook.api.extension.request.ExtensionRespondRequest;
import com.e207.woojoobook.domain.exchange.TradeUserCondition;
import com.e207.woojoobook.domain.extension.Extension;
import com.e207.woojoobook.domain.extension.ExtensionRepository;
import com.e207.woojoobook.domain.extension.ExtensionStatus;
import com.e207.woojoobook.domain.rental.Rental;
import com.e207.woojoobook.domain.rental.RentalRepository;
import com.e207.woojoobook.domain.rental.RentalStatus;
import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.global.exception.ErrorCode;
import com.e207.woojoobook.global.exception.ErrorException;
import com.e207.woojoobook.global.helper.UserHelper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ExtensionService {

	private final RentalRepository rentalRepository;
	private final ExtensionRepository extensionRepository;
	private final UserHelper userHelper;
	private final ApplicationEventPublisher eventPublisher;

	@Transactional
	public Long extensionRental(Long rentalId) {
		Rental rental = validateAndFindRental(rentalId);

		Extension extension = Extension.builder()
			.rental(rental)
			.createdAt(LocalDateTime.now())
			.build();

		Extension save = this.extensionRepository.save(extension);
		this.eventPublisher.publishEvent(new ExtensionEvent(rental));
		return save.getId();
	}

	@Transactional
	public Page<ExtensionResponse> findByCondition(ExtensionFindCondition conditionForFind, Pageable pageable) {
		Long userId = userHelper.findCurrentUser().getId();
		TradeUserCondition userCondition = conditionForFind.userCondition();
		ExtensionStatus extensionStatus = conditionForFind.extensionStatus();
		return extensionRepository.findByStatusAndUserCondition(userId, extensionStatus, userCondition, pageable)
			.map(ExtensionResponse::of);
	}

	@Transactional
	public void respond(Long extensionId, ExtensionRespondRequest request) {
		Extension extension = validateAndFindExtension(extensionId);
		Rental rental = extension.getRental();
		User owner = rental.getUserbook().getUser();
		User currentUser = this.userHelper.findCurrentUser();

		validateIsOwner(owner, currentUser);
		validateRentalStatus(rental);
		updateRentalAndExtension(request, rental, extension);
		Extension savedExtension = this.extensionRepository.save(extension);

		eventPublisher.publishEvent(new ExtensionResultEvent(rental, savedExtension.getExtensionStatus()));
	}

	@Transactional
	public void delete(Long extensionId) {
		Extension extension = validateAndFindExtension(extensionId);
		User user = extension.getRental().getUser();
		User currentUser = this.userHelper.findCurrentUser();
		validateIsOwner(user, currentUser);
		this.extensionRepository.delete(extension);
	}

	private void updateRentalAndExtension(ExtensionRespondRequest request, Rental rental, Extension extension) {
		rental.extension(request.isApproved());
		extension.respond(request.isApproved());
		this.rentalRepository.save(rental);
	}

	private Extension validateAndFindExtension(Long extensionId) {
		Extension extension = this.extensionRepository.findById(extensionId)
			.orElseThrow(() -> new ErrorException(ErrorCode.NotFound));
		if (extension.getExtensionStatus() != OFFERING) {
			throw new ErrorException(ErrorCode.InvalidAccess);
		}
		return extension;
	}

	private static void validateIsOwner(User owner, User currentUser) {
		if (owner.getId() != currentUser.getId()) {
			throw new ErrorException(ErrorCode.ForbiddenError);
		}
	}

	private Rental validateAndFindRental(Long rentalId) {
		Rental rental = this.rentalRepository.findById(rentalId)
			.orElseThrow(() -> new ErrorException(ErrorCode.NotFound));
		User currentUser = this.userHelper.findCurrentUser();
		if (currentUser.getId() != rental.getUser().getId()) {
			throw new ErrorException(ErrorCode.ForbiddenError);
		}
		validateRentalStatus(rental);
		return rental;
	}

	private static void validateRentalStatus(Rental rental) {
		if (rental.getRentalStatus() != RentalStatus.IN_PROGRESS) {
			throw new ErrorException(ErrorCode.InvalidAccess);
		}
	}
}
