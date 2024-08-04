package com.e207.woojoobook.domain.userbook;

import static com.e207.woojoobook.domain.userbook.QUserbook.*;
import static com.e207.woojoobook.domain.userbook.QWishbook.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.e207.woojoobook.domain.user.User;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

public class WishbookFindRepositoryImpl implements WishbookFindRepository {

	private final JPAQueryFactory queryFactory;

	public WishbookFindRepositoryImpl(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}

	@Override
	public Page<Wishbook> findWishbookPageWithUserbookByUser(User user, Pageable pageable) {
		List<Wishbook> content = this.queryFactory.selectFrom(wishbook)
			.join(wishbook.userbook, userbook)
			.fetchJoin()
			.where(wishbook.user.eq(user))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long count = countWishbookByUser(user);

		return new PageImpl<>(content, pageable, count);
	}

	@Override
	public Long countWishbookByUser(User user) {
		return this.queryFactory.select(wishbook.count()).from(wishbook).where(wishbook.user.eq(user)).fetchOne();
	}
}
