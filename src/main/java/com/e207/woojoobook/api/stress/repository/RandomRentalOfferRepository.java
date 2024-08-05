package com.e207.woojoobook.api.stress.repository;

import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.e207.woojoobook.domain.exchange.Exchange;
import com.e207.woojoobook.domain.extension.Extension;
import com.e207.woojoobook.domain.rental.Rental;

@Profile("stress")
public interface RandomRentalOfferRepository extends CrudRepository<Rental, Long> {

	/**
	 * 사용자가 받은 대여 요청 중 하나를 조회
	 */
	@Query(nativeQuery = true,
		value = "select r.* "
			+ "from rental r "
			+ "join userbook ub "
			+ "on r.userbook_id = ub.id "
			+ "and r.start_date is null "
			+ "and ub.user_id = :userId "
			+ "order by rand() "
			+ "limit 1")
	Optional<Rental> findReceivedRentalOffer(@Param("userId") Long userId);
}
