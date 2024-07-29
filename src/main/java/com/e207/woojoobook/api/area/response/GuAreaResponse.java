package com.e207.woojoobook.api.area.response;

import com.e207.woojoobook.domain.area.GuArea;

import lombok.Builder;

@Builder
public record GuAreaResponse(String siCode, String guCode, String guName) {

	public static GuAreaResponse of(GuArea guArea) {
		return GuAreaResponse.builder()
			.siCode(guArea.getGuId().getSiArea().getSiCode())
			.guCode(guArea.getGuId().getGuCode())
			.guName(guArea.getGuName())
			.build();
	}
}
