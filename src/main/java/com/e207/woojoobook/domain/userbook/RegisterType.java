package com.e207.woojoobook.domain.userbook;

import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RegisterType {
	RENTAL_EXCHANGE(TradeStatus.RENTAL_EXCHANGE_AVAILABLE),
	RENTAL(TradeStatus.RENTAL_AVAILABLE),
	EXCHANGE(TradeStatus.EXCHANGE_AVAILABLE),
	INACTIVE(TradeStatus.UNAVAILABLE);

	private final TradeStatus defaultTradeStatus;
	private static final Set<RegisterType> RentalAllowed = Set.of(RENTAL_EXCHANGE, RENTAL);
	private static final Set<RegisterType> ExchangeAllowed = Set.of(RENTAL_EXCHANGE, EXCHANGE);

	public boolean canRent() {
		return RentalAllowed.contains(this);
	}

	public boolean canExchange() {
		return ExchangeAllowed.contains(this);
	}

	public RegisterType rentalOn() {
		return switch (this) {
			case EXCHANGE -> RENTAL_EXCHANGE;
			case INACTIVE -> RENTAL;
			default -> this;
		};
	}

	public RegisterType rentalOff() {
		return switch (this) {
			case RENTAL_EXCHANGE -> EXCHANGE;
			case RENTAL -> INACTIVE;
			default -> this;
		};
	}

	public RegisterType exchangeOn() {
		return switch (this) {
			case RENTAL -> RENTAL_EXCHANGE;
			case INACTIVE -> EXCHANGE;
			default -> this;
		};
	}

	public RegisterType exchangeOff() {
		return switch (this) {
			case RENTAL_EXCHANGE -> RENTAL;
			case EXCHANGE -> INACTIVE;
			default -> this;
		};
	}
}