package com.e207.woojoobook.domain.area;

import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Embeddable
public class DongId {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({@JoinColumn(name = "guCode"), @JoinColumn(name = "siCode")})
	private GuArea guArea;
	private String dongCode;

	public String toAreaCode() {
		return guArea.getGuId().getSiArea().getSiCode() + guArea.getGuId().getGuCode() + dongCode;
	}
}
