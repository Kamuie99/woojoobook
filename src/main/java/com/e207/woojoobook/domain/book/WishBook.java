package com.e207.woojoobook.domain.book;

import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.userbook.Userbook;

import jakarta.persistence.Entity;
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
public class WishBook {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne
	private User user;
	@ManyToOne
	private Userbook userbook;

	@Builder
	public WishBook(User user, Userbook userbook) {
		this.user = user;
		this.userbook = userbook;
	}
}
