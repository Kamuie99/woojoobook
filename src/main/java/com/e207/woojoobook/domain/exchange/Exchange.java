package com.e207.woojoobook.domain.exchange;

import static com.e207.woojoobook.domain.exchange.ExchangeStatus.*;

import java.time.LocalDateTime;

import com.e207.woojoobook.domain.userbook.Userbook;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Exchange {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private Userbook senderBook;

	@ManyToOne(fetch = FetchType.LAZY)
	private Userbook receiverBook;

	private LocalDateTime exchangeDate;

	private ExchangeStatus exchangeStatus; // TODO <jhl221123> rental, exchange 통합 고려

	@Builder
	private Exchange(Long id, Userbook senderBook, Userbook receiverBook) {
		this.id = id;
		this.senderBook = senderBook;
		this.receiverBook = receiverBook;
		this.exchangeStatus = IN_PROGRESS;
	}

	public void respond(boolean isApproved) {
		if (isApproved) {
			this.exchangeDate = LocalDateTime.now();
			this.exchangeStatus = COMPLETED;
		} else {
			this.exchangeStatus = REJECTED;
		}
	}

	public void registerExchangeDate(LocalDateTime exchangeDate) {
		this.exchangeDate = exchangeDate;
	}

	public boolean isOffering() {
		return this.exchangeStatus == IN_PROGRESS;
	}
}
