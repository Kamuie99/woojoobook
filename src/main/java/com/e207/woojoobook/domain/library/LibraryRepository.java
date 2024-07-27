package com.e207.woojoobook.domain.library;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LibraryRepository extends JpaRepository<Library, Long> {

	List<Library> findByUserId(Long userId);

	Optional<Library> findByIdAndUserId(Long libraryId, Long userId);

	@Query("select MAX(l.orderNumber) from Library l")
	Optional<Long> findMaxOrderNumber();
}