package com.e207.woojoobook.domain.userbook;

import com.e207.woojoobook.domain.book.Book;
import com.e207.woojoobook.domain.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Entity
public class Userbook {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private Book book;

	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
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

	public void inactivate() {
		this.tradeStatus = TradeStatus.UNAVAILABLE;
	}

	public boolean isAvailable() {
		if (this.tradeStatus == TradeStatus.UNAVAILABLE || this.tradeStatus == TradeStatus.EXCHANGED
			|| this.tradeStatus == TradeStatus.RENTED) {
			return false;
		}
		return true;
	}

	public boolean isPossibleToChangeRegisterType() {
		return this.tradeStatus.isPossibleToChangeRegisterType();
	}

	public boolean canUpdate() {
		return this.tradeStatus != TradeStatus.EXCHANGED;
	}

	public void removeUser() {
		this.user = null;
	}

	public void updateTradeStatus(TradeStatus tradeStatus) {
		this.tradeStatus = tradeStatus;
	}

	public void updateRegisterType(RegisterType registerType) {
		this.registerType = registerType;
		this.tradeStatus = this.registerType.getDefaultTradeStatus();
	}

	public void updateQualityStatus(QualityStatus qualityStatus) {
		this.qualityStatus = qualityStatus;
	}

}