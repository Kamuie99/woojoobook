package com.e207.woojoobook.api.rental.request;

import com.e207.woojoobook.domain.rental.RentalStatus;
import com.e207.woojoobook.domain.rental.RentalUserCondition;

public record RentalFindCondition(RentalUserCondition userCondition, RentalStatus rentalStatus) {
}
