package com.e207.woojoobook.api.chatroom.request;

import lombok.Builder;

@Builder
public record ChatRoomRequest(Long senderId, Long receiverId) {
}
