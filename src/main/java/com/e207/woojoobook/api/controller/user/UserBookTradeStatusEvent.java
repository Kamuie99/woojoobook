package com.e207.woojoobook.api.controller.user;

import com.e207.woojoobook.domain.userbook.TradeStatus;
import com.e207.woojoobook.domain.userbook.Userbook;

public record UserBookTradeStatusEvent(Userbook userbook, TradeStatus tradeStatus) {
}
