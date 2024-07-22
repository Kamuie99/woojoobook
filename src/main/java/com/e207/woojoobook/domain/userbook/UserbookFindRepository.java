package com.e207.woojoobook.domain.userbook;

import java.util.List;

import org.springframework.data.domain.Pageable;

public interface UserbookFindRepository {
	List<Userbook> findUserbookList(UserbookFindCondition condition, Pageable pageable);
	Long countUserbook(UserbookFindCondition condition);
}
