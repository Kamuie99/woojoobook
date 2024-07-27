package com.e207.woojoobook.api.userbook.request;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.e207.woojoobook.domain.userbook.RegisterType;
import com.e207.woojoobook.domain.userbook.UserbookFindCondition;

public record UserbookPageFindRequest(String keyword, List<String> areaCodeList, RegisterType registerType) {

	public UserbookPageFindRequest {
		if (Objects.isNull(areaCodeList)) {
			areaCodeList = new ArrayList<>();
		}
	}

	public UserbookFindCondition toCondition() {
		return new UserbookFindCondition(keyword, areaCodeList, registerType);
	}
}
