package com.e207.woojoobook.api.stress.repository;

import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.e207.woojoobook.domain.extension.Extension;

@Profile("stress")
public interface RandomExtensionOfferRepository extends CrudRepository<Extension, Long> {

	/**
	 * 사용자가 받은 연장 요청 중 하나를 조회
	 */
	@Query(nativeQuery = true,
		value = "select e.* "
			+ "from extension e "
			+ "join rental r "
			+ "on e.rental_id = r.id "
			+ "and e.extension_status = 0 "
			+ "join userbook ub "
			+ "on ub.id = r.userbook_id "
			+ "and ub.user_id = :userId "
			+ "order by rand() "
			+ "limit 1")
	Optional<Extension> findReceivedExtensionOffer(@Param("userId") Long userId);
}
