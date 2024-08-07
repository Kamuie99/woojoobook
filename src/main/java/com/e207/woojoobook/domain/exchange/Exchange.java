package com.e207.woojoobook.domain.exchange;

import static com.e207.woojoobook.domain.exchange.ExchangeStatus.*;

import java.time.LocalDateTime;

import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.userbook.Userbook;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Exchange {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private User sender;

	@ManyToOne(fetch = FetchType.LAZY)
	private User receiver;

	@ManyToOne(fetch = FetchType.LAZY)
	private Userbook senderBook;

	@ManyToOne(fetch = FetchType.LAZY)
	private Userbook receiverBook;

	private LocalDateTime exchangeDate;

	@Enumerated(EnumType.STRING)
	private ExchangeStatus exchangeStatus;

	@Builder
	private Exchange(Long id, User sender, User receiver, Userbook senderBook, Userbook receiverBook) {
		this.id = id;
		this.sender = sender;
		this.receiver = receiver;
		this.senderBook = senderBook;
		this.receiverBook = receiverBook;
		this.exchangeStatus = IN_PROGRESS;
	}

	public void respond(ExchangeStatus status) {
		if (APPROVED.equals(status))
			this.exchangeDate = LocalDateTime.now();
		this.exchangeStatus = status;
	}

	public boolean isOffering() {
		return this.exchangeStatus == IN_PROGRESS;
	}
}
