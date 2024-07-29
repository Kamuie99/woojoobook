package com.e207.woojoobook.api.area.response;

import com.e207.woojoobook.domain.area.Area;

import lombok.Builder;

@Builder
public record AreaResponse(String areaCode, String siName, String guName, String dongName) {

	public static AreaResponse of(Area area) {
		return builder().areaCode(area.toAreaCode())
			.siName(area.siArea().getSiName())
			.guName(area.guArea().getGuName())
			.dongName(area.dongArea().getDongName())
			.build();
	}
}
