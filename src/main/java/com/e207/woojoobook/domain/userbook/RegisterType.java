package com.e207.woojoobook.domain.userbook;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RegisterType {
	RENTAL_EXCHANGE(TradeStatus.RENTAL_EXCHANGE_AVAILABLE),
	RENTAL(TradeStatus.RENTAL_AVAILABLE),
	EXCHANGE(TradeStatus.EXCHANGE_AVAILABLE);

	private final TradeStatus defaultTradeStatus;
}