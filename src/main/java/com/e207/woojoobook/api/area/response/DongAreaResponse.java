package com.e207.woojoobook.api.area.response;

import com.e207.woojoobook.domain.area.DongArea;

import lombok.Builder;

@Builder
public record DongAreaResponse(String areaCode, String dongName) {

	public static DongAreaResponse of(DongArea dongArea) {
		return DongAreaResponse.builder()
			.areaCode(dongArea.getDongId().toAreaCode())
			.dongName(dongArea.getDongName())
			.build();
	}
}
