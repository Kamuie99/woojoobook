package com.e207.woojoobook.api.controller.userbook.request;

import com.e207.woojoobook.domain.userbook.QualityStatus;
import com.e207.woojoobook.domain.userbook.RegisterType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UserbookCreateRequest(@NotNull String isbn, @NotNull RegisterType registerType,
									@NotNull QualityStatus quality) {
}
