package com.e207.woojoobook.api.book.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record BookFindRequest(@NotBlank String keyword, @NotNull Integer page) {
}