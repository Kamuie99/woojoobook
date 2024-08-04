package com.e207.woojoobook.domain.userbook;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.e207.woojoobook.domain.user.User;

public interface WishbookFindRepository {

	Page<Wishbook> findWishbookPageWithUserbookByUser(User user, Pageable pageable);

	Long countWishbookByUser(User user);
}
