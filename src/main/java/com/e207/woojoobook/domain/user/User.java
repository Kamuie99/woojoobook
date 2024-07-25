package com.e207.woojoobook.domain.user;

import java.util.ArrayList;
import java.util.List;

import com.e207.woojoobook.domain.user.experience.Experience;
import com.e207.woojoobook.domain.user.point.Point;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "users")
@Entity
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String email;
	private String password;
	private String nickname;
	private String areaCode;
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Point> points = new ArrayList<>();
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Experience> experiences = new ArrayList<>();

	@Builder
	private User(String email, String password, String nickname, String areaCode) {
		this.email = email;
		this.password = password;
		this.nickname = nickname;
		this.areaCode = areaCode;
	}

	public void update(String nickname, String areaCode) {
		this.nickname = nickname;
		this.areaCode = areaCode;
	}

	public void updatePassword(String password) {
		this.password = password;
	}
}
