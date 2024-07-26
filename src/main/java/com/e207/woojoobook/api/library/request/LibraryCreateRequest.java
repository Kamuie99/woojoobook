package com.e207.woojoobook.api.library.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record LibraryCreateRequest(@NotBlank String categoryName, @NotBlank String books) {
}
