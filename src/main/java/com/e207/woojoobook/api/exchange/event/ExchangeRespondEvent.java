package com.e207.woojoobook.api.exchange.event;

import com.e207.woojoobook.domain.exchange.Exchange;

import lombok.Builder;

@Builder
public record ExchangeRespondEvent(Exchange exchange) {
}
