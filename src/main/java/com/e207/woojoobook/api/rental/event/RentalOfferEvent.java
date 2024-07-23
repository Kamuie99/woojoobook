package com.e207.woojoobook.api.rental.event;

import com.e207.woojoobook.domain.rental.Rental;

public record RentalOfferEvent(Rental rental, boolean isApproved) {
}
