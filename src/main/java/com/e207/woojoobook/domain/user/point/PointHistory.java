package com.e207.woojoobook.domain.user.point;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PointHistory {
	BOOK_REGISTER(200),
	BOOK_RENTAL(200),
	BOOK_EXCHANGE(100),
	USE_BOOK_RENTAL(-100),
	OVERDUE(-200),
	ATTENDANCE(10);

	private final int amount;
}
