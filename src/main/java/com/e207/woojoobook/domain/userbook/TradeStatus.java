package com.e207.woojoobook.domain.userbook;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TradeStatus {
	RENTAL_EXCHANGE_AVAILABLE("대여, 교환 가능"),
	RENTAL_AVAILABLE("대여 가능"),
	RENTED("대여중"),
	EXCHANGE_AVAILABLE("교환 가능"),
	EXCHANGED("교환완료"),
	UNAVAILABLE("거래 불가능");

	private final String description;
}