package com.e207.woojoobook.api.chatroom.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ChatRoomRequest(@NotNull Long senderId, @NotNull Long receiverId) {
}
