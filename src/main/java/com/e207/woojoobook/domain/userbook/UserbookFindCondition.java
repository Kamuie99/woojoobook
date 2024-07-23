package com.e207.woojoobook.domain.userbook;

import java.util.List;

import com.e207.woojoobook.api.userbook.request.UserbookPageFindRequest;

import lombok.Builder;

@Builder
public record UserbookFindCondition(String keyword, List<String> areaCodeList, RegisterType registerType) {

	public static UserbookFindCondition of(UserbookPageFindRequest request) {
		return new UserbookFindCondition(request.keyword(), request.areaCodeList(), request.registerType());
	}
}
