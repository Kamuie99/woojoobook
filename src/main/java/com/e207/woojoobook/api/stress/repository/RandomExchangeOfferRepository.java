package com.e207.woojoobook.api.stress.repository;

import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.e207.woojoobook.domain.exchange.Exchange;

@Profile("stress")
public interface RandomExchangeOfferRepository extends CrudRepository<Exchange, Long> {

	/**
	 * 사용자가 받은 교환 요청 중 하나를 조회
	 */
	@Query(nativeQuery = true,
		value = "select e.* "
			+ "from exchange e "
			+ "join userbook ub "
			+ "on e.receiver_book_id = ub.id "
			+ "and e.exchange_date is null "
			+ "and ub.user_id = :userId "
			+ "order by rand() "
			+ "limit 1")
	Optional<Exchange> findReceivedExchangeOffer(@Param("userId") Long userId);
}
