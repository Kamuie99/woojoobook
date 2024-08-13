package com.e207.woojoobook.domain.userbook;

import static com.e207.woojoobook.domain.book.QBook.*;
import static com.e207.woojoobook.domain.user.QUser.*;
import static com.e207.woojoobook.domain.userbook.QUserbook.*;
import static com.e207.woojoobook.domain.userbook.QWishbook.*;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.e207.woojoobook.api.book.response.BookResponse;
import com.e207.woojoobook.api.user.response.UserResponse;
import com.e207.woojoobook.api.userbook.request.UserbookListRequest;
import com.e207.woojoobook.api.userbook.response.UserbookWithLike;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

@Repository
public class UserbookQueryRepository {

	private static final BooleanExpression ALL_TRADE_EXPRESSION = userbook.tradeStatus.in(TradeStatus.RENTAL_AVAILABLE,
		TradeStatus.EXCHANGE_AVAILABLE, TradeStatus.RENTAL_EXCHANGE_AVAILABLE);
	private static final BooleanExpression RENTAL_TRADE_EXPRESSION = userbook.tradeStatus.in(
		TradeStatus.RENTAL_AVAILABLE, TradeStatus.RENTAL_EXCHANGE_AVAILABLE);
	private static final BooleanExpression EXCHANGE_TRADE_EXPRESSION = userbook.tradeStatus.in(
		TradeStatus.EXCHANGE_AVAILABLE, TradeStatus.RENTAL_EXCHANGE_AVAILABLE);
	private static final int PAGE_SIZE = 20;

	private final JPAQueryFactory queryFactory;

	public UserbookQueryRepository(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}

	public List<UserbookWithLike> findUserbookListWithLikeStatus(Long userId, UserbookListRequest request) {
		return queryFactory.select(
			Projections.constructor(
				UserbookWithLike.class,
				userbook.id,
				Projections.constructor(BookResponse.class,
					userbook.book.isbn,
					userbook.book.title,
					userbook.book.author,
					userbook.book.publisher,
					userbook.book.publicationDate,
					userbook.book.thumbnail,
					userbook.book.description
				),
				Projections.constructor(UserResponse.class,
					userbook.user.id,
					userbook.user.nickname,
					userbook.user.email
				),
				userbook.registerType,
				userbook.tradeStatus,
				userbook.qualityStatus,
				userbook.areaCode,
				wishbook.isNotNull()
			))
			.from(userbook)
			.join(userbook.book, book)
			.join(userbook.user, user)
			.leftJoin(wishbook)
			.on(wishbook.user.id.eq(userId).and(wishbook.userbook.eq(userbook)))
			.where(
				eqAreaCode(request.areaCode()),
				likeKeyword(request.keyword()),
				gtUserbookId(request.userbookId())
			).orderBy(userbook.id.desc())
			.limit(PAGE_SIZE)
			.fetch();
	}

	private BooleanExpression eqAreaCode(String areaCode) {
		if (StringUtils.isEmpty(areaCode)) {
			return null;
		}
		return userbook.areaCode.eq(areaCode);
	}

	private BooleanExpression gtUserbookId(Long userbookId) {
		if(userbookId == null) {
			return null;
		}
		return userbook.id.gt(userbookId);
	}

	private BooleanExpression likeKeyword(String keyword) {
		if (StringUtils.isEmpty(keyword)) {
			return null;
		}
		return book.title.contains(keyword)
			.or(book.author.contains(keyword))
			.or(book.publisher.contains(keyword))
			.or(book.isbn.eq(keyword))
			.or(book.description.contains(keyword));
	}

	public Page<Userbook> findMyExchangablePage(MyExchangableUserbookCondition condition, Pageable pageable) {
		BooleanExpression[] myExchangableExpressions = {hasRegisterType(RegisterType.EXCHANGE),
			canExecuteTrade(RegisterType.EXCHANGE), isOwnedByUser(condition.userId())};
		return findPageWithExpressions(pageable, myExchangableExpressions);
	}

	public Page<Userbook> findMyPage(MyUserbookCondition condition, Pageable pageable) {
		BooleanExpression[] myUserbookExpressions = {isOwnedByUser(condition.userId()),
			isTradeStatus(condition.tradeStatus()), isNotInactive(), isNotExchanged()};
		return findPageWithExpressions(pageable, myUserbookExpressions);
	}

	private Page<Userbook> findPageWithExpressions(Pageable pageable, BooleanExpression... expressions) {
		List<Userbook> content = this.queryFactory.selectFrom(userbook)
			.join(userbook.book, book)
			.fetchJoin()
			.where(expressions)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long count = this.countWithExpressions(expressions);
		return new PageImpl<>(content, pageable, count);
	}

	private Long countWithExpressions(BooleanExpression... expressions) {
		return this.queryFactory.select(userbook.count()).from(userbook).where(expressions).fetchOne();
	}

	private BooleanExpression isKeywordInTitleOrAuthor(String keyword) {
		return StringUtils.hasText(keyword) ?
			book.title.contains(keyword.trim()).or(book.author.contains(keyword.trim())) : null;
	}

	private BooleanExpression isAreaCodeInList(List<String> codeList) {
		return codeList.isEmpty() ? null : userbook.areaCode.in(codeList);
	}

	private BooleanExpression canExecuteTrade(RegisterType registerType) {
		if (Objects.isNull(registerType)) {
			return ALL_TRADE_EXPRESSION;
		}

		return switch (registerType) {
			case RENTAL -> RENTAL_TRADE_EXPRESSION;
			case EXCHANGE -> EXCHANGE_TRADE_EXPRESSION;
			default -> ALL_TRADE_EXPRESSION;
		};
	}

	private BooleanExpression hasRegisterType(RegisterType registerType) {
		return Objects.isNull(registerType) ? null :
			userbook.registerType.eq(registerType).or(userbook.registerType.eq(RegisterType.RENTAL_EXCHANGE));
	}

	private BooleanExpression isOwnedByUser(Long userId) {
		return userId == null ? null : userbook.user.id.eq(userId);
	}

	private BooleanExpression isNotInactive() {
		return userbook.registerType.ne(RegisterType.INACTIVE).and(userbook.tradeStatus.ne(TradeStatus.UNAVAILABLE));
	}

	private BooleanExpression isTradeStatus(TradeStatus tradeStatus) {
		if (tradeStatus == null) {
			return null;
		}

		boolean canRent = tradeStatus.canRent();
		boolean canExchange = tradeStatus.canExchange();

		if (canRent && canExchange) {
			return ALL_TRADE_EXPRESSION;
		}

		if (canExchange) {
			return EXCHANGE_TRADE_EXPRESSION;
		}

		if (canRent) {
			return RENTAL_TRADE_EXPRESSION;
		}

		return userbook.tradeStatus.eq(tradeStatus);
	}

	private BooleanExpression isNotExchanged() {
		return userbook.tradeStatus.ne(TradeStatus.EXCHANGED);
	}
}
