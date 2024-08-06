package com.e207.woojoobook.domain.extension;

import static com.e207.woojoobook.domain.extension.QExtension.*;
import static com.e207.woojoobook.domain.rental.QRental.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import com.e207.woojoobook.domain.exchange.TradeUserCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

public class ExtensionRepositoryCustomImpl implements ExtensionRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	public ExtensionRepositoryCustomImpl(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}

	@Override
	public boolean existsExtensionByExtensionStatus(Long extensionId, ExtensionStatus status) {
		Integer fetchFirst = queryFactory.selectOne()
			.from(extension)
			.where(
				extension.rental.id.eq(extensionId)
					.and(extension.extensionStatus.eq(status))
			)
			.fetchFirst();

		return fetchFirst != null;
	}

	@Override
	public Page<Extension> findByStatusAndUserCondition(Long userId, ExtensionStatus extensionStatus,
		TradeUserCondition condition, Pageable pageable) {

		List<Extension> content = queryFactory
			.selectFrom(extension)
			.where(
				extension.extensionStatus.eq(extensionStatus),
				checkUserCondition(userId, condition)
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		JPAQuery<Extension> countQuery = queryFactory
			.selectFrom(extension)
			.where(
				extension.extensionStatus.eq(extensionStatus),
				checkUserCondition(userId, condition)
			);

		return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchCount);
	}

	private BooleanExpression checkUserCondition(Long userId, TradeUserCondition condition) {
		return switch (condition) {
			case SENDER -> extension.rental.user.id.eq(userId);
			case RECEIVER -> extension.rental.userbook.user.id.eq(userId);
			case SENDER_RECEIVER ->
				extension.rental.user.id.eq(userId).or(extension.rental.userbook.user.id.eq(userId));
		};
	}
}
