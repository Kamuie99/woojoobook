package com.e207.woojoobook.domain.extension;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ExtensionStatus {
	OFFERING("신청"), REJECTED("거절"), APPROVED("승인");

	private final String description;
}
