package com.e207.woojoobook.api.userbook.response;

import com.e207.woojoobook.api.book.response.BookResponse;
import com.e207.woojoobook.api.user.response.UserResponse;
import com.e207.woojoobook.domain.userbook.QualityStatus;
import com.e207.woojoobook.domain.userbook.RegisterType;
import com.e207.woojoobook.domain.userbook.TradeStatus;

public record UserbookWithLike(Long userbookid, BookResponse book, UserResponse user, RegisterType registerType, TradeStatus tradeStatus,
							   QualityStatus qualityStatus, String areaCode, Boolean likeStatus) {
}
