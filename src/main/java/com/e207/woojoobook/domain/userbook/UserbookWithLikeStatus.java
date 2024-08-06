package com.e207.woojoobook.domain.userbook;

import com.e207.woojoobook.domain.book.Book;
import com.e207.woojoobook.domain.user.User;
import com.querydsl.core.annotations.QueryProjection;

public record UserbookWithLikeStatus(Long id, Book book, User user, RegisterType registerType, TradeStatus tradeStatus,
									 QualityStatus qualityStatus, String areaCode, Boolean likeStatus) {

	@QueryProjection
	public UserbookWithLikeStatus(Long id, Book book, User user, RegisterType registerType, TradeStatus tradeStatus,
		QualityStatus qualityStatus, String areaCode, Boolean likeStatus) {
		this.id = id;
		this.book = book;
		this.user = user;
		this.registerType = registerType;
		this.tradeStatus = tradeStatus;
		this.qualityStatus = qualityStatus;
		this.areaCode = areaCode;
		this.likeStatus = likeStatus;
	}
}
