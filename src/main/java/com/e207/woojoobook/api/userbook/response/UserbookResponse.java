package com.e207.woojoobook.api.userbook.response;

import com.e207.woojoobook.api.book.response.BookItem;
import com.e207.woojoobook.api.user.response.UserResponse;
import com.e207.woojoobook.domain.userbook.QualityStatus;
import com.e207.woojoobook.domain.userbook.RegisterType;
import com.e207.woojoobook.domain.userbook.TradeStatus;
import com.e207.woojoobook.domain.userbook.Userbook;

import lombok.Builder;

@Builder
public record UserbookResponse(Long id, BookItem bookInfo, UserResponse ownerInfo, RegisterType registerType,
							   TradeStatus tradeStatus, QualityStatus qualityStatus, String areaCode) {

	public static UserbookResponse of(Userbook userbook) {
		return UserbookResponse.builder()
			.id(userbook.getId())
			.bookInfo(BookItem.of(userbook.getBook()))
			.ownerInfo(UserResponse.of(userbook.getUser()))
			.registerType(userbook.getRegisterType())
			.tradeStatus(userbook.getTradeStatus())
			.qualityStatus(userbook.getQualityStatus())
			.areaCode(userbook.getAreaCode())
			.build();
	}
}
