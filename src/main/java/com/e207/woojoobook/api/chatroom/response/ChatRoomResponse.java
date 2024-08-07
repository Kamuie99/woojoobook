package com.e207.woojoobook.api.chatroom.response;

import com.e207.woojoobook.domain.chatroom.ChatRoom;

import lombok.Builder;

@Builder
public record ChatRoomResponse(Long id, Long senderId, Long receiverId, String senderNickname,
							   String receiverNickname) {

	public static ChatRoomResponse of(ChatRoom chatRoom) {
		return ChatRoomResponse.builder()
			.id(chatRoom.getId())
			.senderId(chatRoom.getSender().getId())
			.receiverId(chatRoom.getReceiver().getId())
			.senderNickname(chatRoom.getSender().getNickname())
			.receiverNickname(chatRoom.getReceiver().getNickname())
			.build();
	}
}
