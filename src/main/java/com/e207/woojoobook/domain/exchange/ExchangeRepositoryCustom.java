package com.e207.woojoobook.domain.exchange;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExchangeRepositoryCustom {
	Page<Exchange> findByStatusAndUserCondition(Long userId, ExchangeStatus exchangeStatus,
		ExchangeUserCondition condition, Pageable pageable);
}
