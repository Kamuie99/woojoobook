package com.e207.woojoobook.api.userbook.request;

import com.e207.woojoobook.domain.userbook.QualityStatus;

import jakarta.validation.constraints.NotNull;

public record UserbookUpdateRequest(@NotNull Boolean canRent, @NotNull Boolean canExchange, @NotNull QualityStatus quality) {
}
