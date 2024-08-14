package com.e207.woojoobook.domain.userbook;

import com.e207.woojoobook.domain.book.Book;
import com.e207.woojoobook.domain.rental.Rental;
import com.e207.woojoobook.domain.user.User;

import io.jsonwebtoken.lang.Assert;
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

import static com.e207.woojoobook.domain.rental.RentalStatus.OFFERING;

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
			|| this.tradeStatus == TradeStatus.RENTED || this.registerType == RegisterType.INACTIVE) {
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

	public void removeUser(User user) {
		this.user = user;
		inactivate();
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

	public Rental createRental(User user) {
		Assert.notNull(user, "유저가 없습니다.");

		if (this.user.getId().equals(user.getId())) {
			throw new IllegalStateException("동일한 유저입니다");
		}

		if (!isAvailable()) {
			throw new IllegalStateException("접근할 수 없는 상태입니다");
		}
		return Rental.builder()
			.user(user)
			.userbook(this)
			.rentalStatus(OFFERING)
			.build();
	}
}