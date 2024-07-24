package com.e207.woojoobook.api.exchange.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ExchangeCreateRequest(@NotNull Long senderBookId, @NotNull Long receiverBookId) {
}
