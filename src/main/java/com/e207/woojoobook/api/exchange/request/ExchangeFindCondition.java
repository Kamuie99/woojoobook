package com.e207.woojoobook.api.exchange.request;

import com.e207.woojoobook.domain.exchange.ExchangeStatus;
import com.e207.woojoobook.domain.exchange.TradeUserCondition;

public record ExchangeFindCondition(TradeUserCondition userCondition, ExchangeStatus exchangeStatus) {
}
