package com.e207.woojoobook.api.userbook;

import com.e207.woojoobook.api.book.response.BookResponse;
import com.e207.woojoobook.api.user.response.UserResponse;
import com.e207.woojoobook.api.userbook.response.UserbookResponse;
import com.e207.woojoobook.domain.userbook.UserbookWithLikeStatus;

public record UserbookWithLikeResponse(UserbookResponse userbook, Boolean like) {

	public static UserbookWithLikeResponse of(UserbookWithLikeStatus dto) {
		UserbookResponse userbookResponse = UserbookResponse.builder()
			.id(dto.id())
			.bookInfo(BookResponse.of(dto.book()))
			.ownerInfo(UserResponse.of(dto.user()))
			.registerType(dto.registerType())
			.tradeStatus(dto.tradeStatus())
			.qualityStatus(dto.qualityStatus())
			.areaCode(dto.user().getAreaCode())
			.build();

		return new UserbookWithLikeResponse(userbookResponse, dto.likeStatus());
	}
}
