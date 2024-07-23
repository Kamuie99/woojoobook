package com.e207.woojoobook.api.chat.request;

import com.e207.woojoobook.domain.chat.Chat;
import com.e207.woojoobook.domain.chatroom.ChatRoom;
import com.e207.woojoobook.domain.user.User;

import lombok.Builder;

@Builder
public record ChatCreateRequest(Long chatRoomId, Long senderId, Long receiverId, String content) {

	public Chat toEntity(ChatRoom chatRoom, User sender) {
		return Chat.builder()
			.chatRoom(chatRoom)
			.sender(sender)
			.content(content)
			.build();
	}
}
