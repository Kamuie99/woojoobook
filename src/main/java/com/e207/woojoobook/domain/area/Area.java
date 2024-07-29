package com.e207.woojoobook.domain.area;

import com.querydsl.core.annotations.QueryProjection;

public record Area(SiArea siArea, GuArea guArea, DongArea dongArea) {

	@QueryProjection
	public Area(SiArea siArea, GuArea guArea, DongArea dongArea) {
		this.siArea = siArea;
		this.guArea = guArea;
		this.dongArea = dongArea;
	}

	public String toAreaCode() {
		return siArea.getSiCode() + guArea.getGuId().getGuCode() + dongArea.getDongId().getDongCode();
	}
}
