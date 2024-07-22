package com.e207.woojoobook.api.controller.userbook.response;

import com.e207.woojoobook.api.controller.book.response.BookResponse;
import com.e207.woojoobook.api.controller.user.response.UserResponse;
import com.e207.woojoobook.domain.userbook.QualityStatus;
import com.e207.woojoobook.domain.userbook.RegisterType;
import com.e207.woojoobook.domain.userbook.TradeStatus;
import com.e207.woojoobook.domain.userbook.Userbook;

import lombok.Builder;

@Builder
public record UserbookResponse(Long id, BookResponse bookInfo, UserResponse ownerInfo, RegisterType registerType,
							   TradeStatus tradeStatus, QualityStatus qualityStatus) {

	public static UserbookResponse of(Userbook userbook) {
		return UserbookResponse.builder()
			.id(userbook.getId())
			.bookInfo(BookResponse.of(userbook.getBook()))
			.ownerInfo(UserResponse.of(userbook.getUser()))
			.registerType(userbook.getRegisterType())
			.tradeStatus(userbook.getTradeStatus())
			.qualityStatus(userbook.getQualityStatus())
			.build();
	}
}
