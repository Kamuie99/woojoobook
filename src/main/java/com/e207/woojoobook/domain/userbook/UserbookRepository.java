package com.e207.woojoobook.domain.userbook;

import org.springframework.data.repository.CrudRepository;

public interface UserbookRepository extends CrudRepository<Userbook, Long>, UserbookFindRepository {
}
