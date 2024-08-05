package com.e207.woojoobook.api.stress.repository;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.e207.woojoobook.domain.book.Book;

@Profile("stress")
public interface RandomBookRepository extends CrudRepository<Book, String> {

	@Query(nativeQuery = true, value = "select * from book order by rand() limit 1")
	Book findRandomBook();
}
