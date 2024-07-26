package com.e207.woojoobook.domain.library;

import com.e207.woojoobook.domain.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Library {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private User user;

	private String name;

	private String bookList;

	private Long orderNumber;

	@Builder
	public Library(User user, String name, String bookList, Long orderNumber) {
		this.user = user;
		this.name = name;
		this.bookList = bookList;
		this.orderNumber = orderNumber;
	}

	public void update(String name, String bookList) {
		this.name = name;
		this.bookList = bookList;
	}

	public void updateOrderNumber(Long newOrderNumber) {
		this.orderNumber = newOrderNumber;
	}
}
