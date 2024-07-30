package com.e207.woojoobook.domain.exchange;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.e207.woojoobook.domain.userbook.Userbook;

public interface ExchangeRepository extends JpaRepository<Exchange, Long>, ExchangeRepositoryCustom {

	@Query("select e from Exchange e"
		+ " join fetch User s on e.sender.id = s.id"
		+ " join fetch User r on e.receiver.id = r.id"
		+ " join fetch Userbook sb on e.senderBook.id = sb.id"
		+ " join fetch Userbook rb on e.receiverBook.id = rb.id"
		+ " where e.id = :id")
	Optional<Exchange> findByIdWithUserbookAndUser(Long id);

	List<Exchange> findAllByReceiverBook(Userbook receiverBook);

	Page<Exchange> findAllByExchangeStatus(ExchangeStatus exchangeStatus, Pageable pageable);
}
