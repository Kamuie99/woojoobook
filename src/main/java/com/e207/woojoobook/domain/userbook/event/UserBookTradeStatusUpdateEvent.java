package com.e207.woojoobook.domain.userbook.event;

import com.e207.woojoobook.domain.userbook.TradeStatus;
import com.e207.woojoobook.domain.userbook.Userbook;

public record UserBookTradeStatusUpdateEvent(Userbook userbook, TradeStatus tradeStatus) {
}
