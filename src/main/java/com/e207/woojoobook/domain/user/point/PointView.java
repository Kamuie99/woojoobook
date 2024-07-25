package com.e207.woojoobook.domain.user.point;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Immutable
@Subselect("SELECT u.id AS user_id, SUM(p.amount) AS total_point FROM users u JOIN point p ON u.id = p.user_id GROUP BY u.id")
@Synchronize("point")
@Getter
@Setter
@Entity
public class PointView {
	@Id
	private Long userId;
	private int totalPoint;
}
