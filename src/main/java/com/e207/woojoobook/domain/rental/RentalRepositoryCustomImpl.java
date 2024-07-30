package com.e207.woojoobook.domain.rental;

import static com.e207.woojoobook.domain.rental.QRental.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

public class RentalRepositoryCustomImpl implements RentalRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	public RentalRepositoryCustomImpl(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}

	@Override
	public Page<Rental> findByStatusAndUserCondition(Long userId, RentalStatus rentalStatus,
		RentalUserCondition condition, Pageable pageable) {

		List<Rental> content = queryFactory
			.selectFrom(rental)
			.where(
				rental.rentalStatus.eq(rentalStatus),
				checkUserCondition(userId, condition)
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		JPAQuery<Rental> countQuery = queryFactory
			.selectFrom(rental)
			.where(
				rental.rentalStatus.eq(rentalStatus),
				checkUserCondition(userId, condition)
			);

		return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchCount);
	}

	private BooleanExpression checkUserCondition(Long userId, RentalUserCondition condition) {
		return switch (condition) {
			case SENDER -> rental.user.id.eq(userId);
			case RECEIVER -> rental.userbook.user.id.eq(userId);
			case SENDER_RECEIVER -> rental.user.id.eq(userId).or(rental.userbook.user.id.eq(userId));
		};
	}
}
