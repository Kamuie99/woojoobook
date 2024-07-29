package com.e207.woojoobook.api.area.request;

import jakarta.validation.constraints.NotNull;

public record GuAreaFindRequest(@NotNull String siCode) {
}
