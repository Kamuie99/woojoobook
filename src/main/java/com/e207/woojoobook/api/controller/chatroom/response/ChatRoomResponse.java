package com.e207.woojoobook.api.controller.chatroom.response;

import com.e207.woojoobook.domain.chatroom.ChatRoom;

import lombok.Builder;

@Builder
public record ChatRoomResponse(Long id, Long senderId, Long receiverId) {

	public static ChatRoomResponse of(ChatRoom chatRoom) {
		return new ChatRoomResponse(
			chatRoom.getId(),
			chatRoom.getSender().getId(),
			chatRoom.getReceiver().getId()
		);
	}
}
