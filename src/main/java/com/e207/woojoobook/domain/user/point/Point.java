package com.e207.woojoobook.domain.user.point;


import com.e207.woojoobook.domain.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Point {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne
	private User user;
	@Enumerated(EnumType.STRING)
	private PointHistory history;
	private int amount;

	@Builder
	public Point(User user, PointHistory history, int amount) {
		this.user = user;
		this.history = history;
		this.amount = amount;
	}
}
