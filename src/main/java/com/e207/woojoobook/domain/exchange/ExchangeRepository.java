package com.e207.woojoobook.domain.exchange;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ExchangeRepository extends JpaRepository<Exchange, Long> {

	@Query("select e from Exchange e"
		+ " join fetch Userbook sb on e.senderBook.id = sb.id"
		+ " join fetch Userbook rb on e.receiverBook.id = rb.id"
		+ " where e.id = :id")
	Optional<Exchange> findFetchById(Long id);
}
