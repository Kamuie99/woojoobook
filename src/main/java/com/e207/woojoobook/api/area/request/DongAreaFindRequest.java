package com.e207.woojoobook.api.area.request;

import jakarta.validation.constraints.NotNull;

public record DongAreaFindRequest(@NotNull String siCode, @NotNull String guCode) {
}