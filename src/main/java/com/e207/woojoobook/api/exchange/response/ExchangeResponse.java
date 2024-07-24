package com.e207.woojoobook.api.exchange.response;

import java.time.LocalDateTime;

import com.e207.woojoobook.api.userbook.response.UserbookResponse;
import com.e207.woojoobook.domain.exchange.Exchange;
import com.e207.woojoobook.domain.exchange.ExchangeStatus;

import lombok.Builder;

@Builder
public record ExchangeResponse(Long id, UserbookResponse senderBook, UserbookResponse receiverBook,
							   LocalDateTime exchangeDate, ExchangeStatus exchangeStatus) {

	public static ExchangeResponse of(Exchange exchange) {
		return ExchangeResponse.builder()
			.id(exchange.getId())
			.senderBook(UserbookResponse.of(exchange.getSenderBook()))
			.receiverBook(UserbookResponse.of(exchange.getReceiverBook()))
			.exchangeDate(exchange.getExchangeDate())
			.exchangeStatus(exchange.getExchangeStatus())
			.build();
	}
}
