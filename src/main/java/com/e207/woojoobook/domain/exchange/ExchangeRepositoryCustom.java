package com.e207.woojoobook.domain.exchange;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExchangeRepositoryCustom {
	Page<Exchange> findAllWithUserConditionAndExchangeStatus(Long userId, ExchangeUserCondition condition,
		ExchangeStatus exchangeStatus, Pageable pageable);
}
