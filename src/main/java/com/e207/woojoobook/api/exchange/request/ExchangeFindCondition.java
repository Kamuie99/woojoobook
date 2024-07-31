package com.e207.woojoobook.api.exchange.request;

import com.e207.woojoobook.domain.exchange.ExchangeStatus;
import com.e207.woojoobook.domain.exchange.ExchangeUserCondition;

public record ExchangeFindCondition(ExchangeUserCondition userCondition, ExchangeStatus exchangeStatus) {
}
