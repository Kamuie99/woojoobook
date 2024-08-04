package com.e207.woojoobook.domain.userbook;

import java.util.List;

import lombok.Builder;

@Builder
public record UserbookFindCondition(String keyword, List<String> areaCodeList, RegisterType registerType, Long userId) {
}
