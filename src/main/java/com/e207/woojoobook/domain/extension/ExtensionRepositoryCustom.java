package com.e207.woojoobook.domain.extension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.e207.woojoobook.domain.exchange.TradeUserCondition;
import com.e207.woojoobook.domain.rental.RentalStatus;

public interface ExtensionRepositoryCustom {
	Page<Extension> findByStatusAndUserCondition(Long userId, ExtensionStatus exchangeStatus,
		TradeUserCondition condition, Pageable pageable);
	boolean existsExtensionByExtensionStatus(Long rentalId, ExtensionStatus status);
}
