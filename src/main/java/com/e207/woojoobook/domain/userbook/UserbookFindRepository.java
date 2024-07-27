package com.e207.woojoobook.domain.userbook;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserbookFindRepository {
	Page<Userbook> findUserbookListByPage(UserbookFindCondition condition, Pageable pageable);
	Long countUserbook(UserbookFindCondition condition);
}
