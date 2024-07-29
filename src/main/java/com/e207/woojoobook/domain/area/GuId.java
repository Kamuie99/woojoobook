package com.e207.woojoobook.domain.area;

import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Embeddable
public class GuId {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "siCode")
	private SiArea siArea;
	private String guCode;
}
