package com.e207.woojoobook.api.area.response;

import com.e207.woojoobook.domain.area.SiArea;

public record SiAreaResponse(String siCode, String siName) {

	public static SiAreaResponse of(SiArea siArea) {
		return new SiAreaResponse(siArea.getSiCode(), siArea.getSiName());
	}
}
