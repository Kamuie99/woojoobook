package com.e207.woojoobook.api.rental;

import com.e207.woojoobook.domain.rental.Rental;

public record RentalOfferEvent(Rental rental, boolean isApproved) {
}
