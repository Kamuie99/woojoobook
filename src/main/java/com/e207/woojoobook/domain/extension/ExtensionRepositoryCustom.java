package com.e207.woojoobook.domain.extension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.e207.woojoobook.domain.exchange.TradeUserCondition;

public interface ExtensionRepositoryCustom {
	Page<Extension> findByStatusAndUserCondition(Long userId, ExtensionStatus exchangeStatus,
		TradeUserCondition condition, Pageable pageable);
}
