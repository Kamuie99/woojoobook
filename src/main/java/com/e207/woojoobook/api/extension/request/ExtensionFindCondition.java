package com.e207.woojoobook.api.extension.request;

import com.e207.woojoobook.domain.exchange.TradeUserCondition;
import com.e207.woojoobook.domain.extension.ExtensionStatus;

public record ExtensionFindCondition(TradeUserCondition userCondition, ExtensionStatus extensionStatus) {
}
