package com.e207.woojoobook.domain.area;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Immutable
@Table(name = "DONG")
@Entity
public class DongArea {

	@EmbeddedId
	private DongId dongId;
	private String dongName;
}
