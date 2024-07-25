package com.e207.woojoobook.domain.user.experience;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Immutable
@Subselect("SELECT u.id AS user_id, SUM(e.amount) AS total_experience FROM users u JOIN experience e ON u.id = e.user_id GROUP BY u.id")
@Synchronize("experience")
@Getter
@Setter
@Entity
public class ExperienceView {
	@Id
	private Long userId;
	private int totalExperience;
}
