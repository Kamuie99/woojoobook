package com.e207.woojoobook.api.library.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record LibraryUpdateRequest(@NotBlank String categoryName, @NotBlank String books) {
}