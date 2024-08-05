package com.e207.woojoobook.api.stress.repository;

import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.e207.woojoobook.domain.userbook.Userbook;

@Profile("stress")
public interface RandomUserbookRepository extends CrudRepository<Userbook, Long> {

	/**
	 * 대여 가능한 책을 아무거나 하나 조회
	 */
	@Query(nativeQuery = true,
		value = "select * "
			+ "from userbook "
			+ "where (trade_status = 'RENTAL_AVAILABLE' or trade_status = 'RENTAL_EXCHANGE_AVAILABLE') "
			+ "and user_id != :userId "
			+ "order by rand() "
			+ "limit 1")
	Optional<Userbook> findRentalOfferAvailable(@Param("userId") Long userId);

	/**
	 * 교환 가능한 책을 아무거나 하나 조회
	 */
	@Query(nativeQuery = true,
		value = "select * "
			+ "from userbook "
			+ "where (trade_status = 'EXCHANGE_AVAILABLE' or trade_status = 'RENTAL_EXCHANGE_AVAILABLE') "
			+ "and user_id != :userId "
			+ "order by rand() "
			+ "limit 1")
	Optional<Userbook> findExchangeOfferAvailable(@Param("userId") Long userId);

	/**
	 * 사용자가 빌린 책 중 하나를 조회
	 */
	@Query(nativeQuery = true,
		value = "select ub.* "
			+ "from userbook ub "
			+ "join rental r "
			+ "on ub.id = r.userbook_id "
			+ "and r.user_id = :userId "
			+ "and r.rental_status = 2 "
			+ "order by rand() "
			+ "limit 1")
	Optional<Userbook> findBorrowed(@Param("userId") Long userId);

	/**
	 * 사용자가 빌려준 책 중 하나를 조회
	 */
	@Query(nativeQuery = true,
		value = "select ub.* "
			+ "from userbook ub "
			+ "join rental r "
			+ "on ub.id = r.userbook_id "
			+ "and r.rental_status = 2 "
			+ "and ub.user_id = :userId "
			+ "order by rand() "
			+ "limit 1")
	Optional<Userbook> findRented(@Param("userId") Long userId);

	/**
	 * 사용자의 교환 가능한 책 중 아무거나 하나 조회
	 */
	@Query(nativeQuery = true,
		value = "select * "
			+ "from userbook "
			+ "where (trade_status = 'EXCHANGE_AVAILABLE' or trade_status = 'RENTAL_EXCHANGE_AVAILABLE') "
			+ "and user_id = :userId "
			+ "order by rand() "
			+ "limit 1")
	Optional<Userbook> findMyExchangeable(@Param("userId") Long userId);
}
