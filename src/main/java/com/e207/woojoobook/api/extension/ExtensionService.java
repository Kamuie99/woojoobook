package com.e207.woojoobook.api.extension;

import com.e207.woojoobook.api.extension.event.ExtensionEvent;
import com.e207.woojoobook.api.extension.event.ExtensionResultEvent;
import com.e207.woojoobook.api.extension.request.ExtensionRespondRequest;
import com.e207.woojoobook.domain.extension.Extension;
import com.e207.woojoobook.domain.extension.ExtensionRepository;
import com.e207.woojoobook.domain.rental.Rental;
import com.e207.woojoobook.domain.rental.RentalRepository;
import com.e207.woojoobook.domain.rental.RentalStatus;
import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.global.helper.UserHelper;

import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
			.orElseThrow(() -> new RuntimeException("존재하지 않는 연장신청입니다."));
		if (extension.getExtensionStatus() != null) {
			throw new RuntimeException("처리된 연장신청에 대한 접근은 허용되지 않습니다.");
		}
		return extension;
	}

	private static void validateIsOwner(User owner, User currentUser) {
		if (owner.getId() != currentUser.getId()) {
			throw new RuntimeException("권한이 없는 접근입니다.");
		}
	}

	private Rental validateAndFindRental(Long rentalId) {
		Rental rental = this.rentalRepository.findById(rentalId)
			.orElseThrow(() -> new RuntimeException("존재하지 않는 대여 정보입니다."));
		User currentUser = this.userHelper.findCurrentUser();
		if (currentUser.getId() != rental.getUser().getId()) {
			throw new RuntimeException("대여 신청자만이 연장 신청을 할 수 있습니다.");
		}
		validateRentalStatus(rental);
		return rental;
	}

	private static void validateRentalStatus(Rental rental) {
		if (rental.getRentalStatus() != RentalStatus.IN_PROGRESS) {
			throw new RuntimeException("연장신청을 할 수 없는 대여 정보입니다.");
		}
	}
}
