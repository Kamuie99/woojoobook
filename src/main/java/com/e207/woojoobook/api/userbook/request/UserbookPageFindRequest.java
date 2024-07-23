package com.e207.woojoobook.api.userbook.request;

import java.util.List;

import com.e207.woojoobook.domain.userbook.RegisterType;

public record UserbookPageFindRequest(String keyword, List<String> areaCodeList, RegisterType registerType) {
}
