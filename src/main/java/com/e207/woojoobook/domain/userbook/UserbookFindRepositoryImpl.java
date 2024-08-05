package com.e207.woojoobook.domain.userbook;

import static com.e207.woojoobook.domain.book.QBook.*;
import static com.e207.woojoobook.domain.userbook.QUserbook.*;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import com.e207.woojoobook.domain.book.Book;
import com.e207.woojoobook.global.util.DynamicQueryHelper;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

public class UserbookFindRepositoryImpl implements UserbookFindRepository {

	private final JPAQueryFactory queryFactory;
	private final DynamicQueryHelper queryHelper;

	public UserbookFindRepositoryImpl(EntityManager em, DynamicQueryHelper queryHelper) {
		this.queryFactory = new JPAQueryFactory(em);
		this.queryHelper = queryHelper;
	}

	@Override
	public Page<Userbook> findUserbookListByPage(UserbookFindCondition condition, Pageable pageable) {
		List<Userbook> content = this.queryFactory.selectFrom(userbook)
			.join(userbook.book, book)
			.where(isKeywordInTitleOrAuthor(condition.keyword()), isAreaCodeInList(condition.areaCodeList()),
				canExecuteTrade(condition.registerType()), hasRegisterType(condition.registerType()),
				isOwnedByUser(condition.userId()))
			.orderBy(this.queryHelper.generateFieldSort(Book.class, pageable.getSort(), "userbook.book"))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long count = this.countUserbook(condition);
		return new PageImpl<>(content, pageable, count);
	}

	@Override
	public Long countUserbook(UserbookFindCondition condition) {
		return this.queryFactory.select(userbook.count())
			.from(userbook)
			.join(userbook.book, book)
			.where(isKeywordInTitleOrAuthor(condition.keyword()), isAreaCodeInList(condition.areaCodeList()),
				canExecuteTrade(condition.registerType()), hasRegisterType(condition.registerType()),
				isOwnedByUser(condition.userId()))
			.fetchOne();
	}

	private BooleanExpression isKeywordInTitleOrAuthor(String keyword) {
		return StringUtils.hasText(keyword) ?
			book.title.contains(keyword.trim()).or(book.author.contains(keyword.trim())) : null;
	}

	private BooleanExpression isAreaCodeInList(List<String> codeList) {
		return codeList.isEmpty() ? null : userbook.areaCode.in(codeList);
	}

	private BooleanExpression canExecuteTrade(RegisterType registerType) {
		BooleanExpression expression;
		BooleanExpression defaultEx = userbook.tradeStatus.eq(TradeStatus.RENTAL_EXCHANGE_AVAILABLE);
		if (Objects.isNull(registerType)) {
			expression = userbook.tradeStatus.eq(TradeStatus.RENTAL_AVAILABLE)
				.or(userbook.tradeStatus.eq(TradeStatus.EXCHANGE_AVAILABLE));
		} else {
			expression = switch (registerType) {
				case RENTAL -> userbook.tradeStatus.eq(TradeStatus.RENTAL_AVAILABLE);
				case EXCHANGE -> userbook.tradeStatus.eq(TradeStatus.EXCHANGE_AVAILABLE);
				default -> userbook.tradeStatus.eq(TradeStatus.RENTAL_AVAILABLE)
					.or(userbook.tradeStatus.eq(TradeStatus.EXCHANGE_AVAILABLE));
			};
		}
		return expression.or(defaultEx);
	}

	private BooleanExpression hasRegisterType(RegisterType registerType) {
		return Objects.isNull(registerType) ? null :
			userbook.registerType.eq(registerType).or(userbook.registerType.eq(RegisterType.RENTAL_EXCHANGE));
	}

	private BooleanExpression isOwnedByUser(Long userId) {
		return userId == null ? null : userbook.user.id.eq(userId);
	}
}
