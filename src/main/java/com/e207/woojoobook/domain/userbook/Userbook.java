package com.e207.woojoobook.domain.userbook;

import com.e207.woojoobook.domain.book.Book;
import com.e207.woojoobook.domain.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class Userbook {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private Book book;

	@ManyToOne
	private User user;

	@Enumerated(EnumType.STRING)
	private RegisterType registerType;

	@Enumerated(EnumType.STRING)
	private TradeStatus tradeStatus;

	@Enumerated(EnumType.STRING)
	private QualityStatus qualityStatus;

	private String areaCode;

	@Builder
	private Userbook(Long id, Book book, User user, RegisterType registerType, TradeStatus tradeStatus,
		QualityStatus qualityStatus) {
		this.id = id;
		this.book = book;
		this.user = user;
		this.registerType = registerType;
		this.tradeStatus = tradeStatus;
		this.qualityStatus = qualityStatus;
		this.areaCode = user.getAreaCode();
	}
}