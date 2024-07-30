package com.e207.woojoobook.domain.rental;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RentalRepositoryCustom {
	Page<Rental> findByStatusAndUserCondition(Long userId, RentalStatus rentalStatus, RentalUserCondition condition,
		Pageable pageable);
}
