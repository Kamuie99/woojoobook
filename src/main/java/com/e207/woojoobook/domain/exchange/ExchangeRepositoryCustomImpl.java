package com.e207.woojoobook.domain.exchange;

import static com.e207.woojoobook.domain.exchange.QExchange.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

public class ExchangeRepositoryCustomImpl implements ExchangeRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	public ExchangeRepositoryCustomImpl(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}

	@Override
	public Page<Exchange> findByStatusAndUserCondition(Long userId, ExchangeStatus exchangeStatus,
		ExchangeUserCondition condition, Pageable pageable) {

		List<Exchange> content = queryFactory
			.selectFrom(exchange)
			.where(
				exchange.exchangeStatus.eq(exchangeStatus),
				checkUserCondition(userId, condition)
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		JPAQuery<Exchange> countQuery = queryFactory
			.selectFrom(exchange)
			.where(
				exchange.exchangeStatus.eq(exchangeStatus),
				checkUserCondition(userId, condition)
			);

		return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchCount);
	}

	private BooleanExpression checkUserCondition(Long userId, ExchangeUserCondition condition) {
		return switch (condition) {
			case SENDER -> exchange.sender.id.eq(userId);
			case RECEIVER -> exchange.receiver.id.eq(userId);
			case SENDER_RECEIVER -> exchange.sender.id.eq(userId).or(exchange.receiver.id.eq(userId));
		};
	}
}
