package com.e207.woojoobook.api.rental.response;

import java.time.LocalDateTime;

import com.e207.woojoobook.api.user.response.UserResponse;
import com.e207.woojoobook.api.userbook.response.UserbookResponse;
import com.e207.woojoobook.domain.rental.Rental;
import com.e207.woojoobook.domain.rental.RentalStatus;

import lombok.Builder;

@Builder
public record RentalResponse(Long id, UserResponse user, UserbookResponse userbook, LocalDateTime startDate,
							 LocalDateTime endDate,
							 RentalStatus rentalStatus, int extensionCount) {

	public static RentalResponse of(Rental rental) {
		return RentalResponse.builder()
			.id(rental.getId())
			.user(UserResponse.of(rental.getUser()))
			.userbook(UserbookResponse.of(rental.getUserbook()))
			.startDate(rental.getStartDate())
			.endDate(rental.getEndDate())
			.rentalStatus(rental.getRentalStatus())
			.extensionCount(rental.getExtensionCount())
			.build();
	}
}
