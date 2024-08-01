package com.e207.woojoobook.domain.user.experience;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ExperienceHistory {
	BOOK_REGISTER(200),
	BOOK_RENTAL(200),
	BOOK_EXCHANGE(100),
	ATTENDANCE(100),
	DELETE_BOOK(-200),
	OVERDUE(-500);

	private final int amount;
}
