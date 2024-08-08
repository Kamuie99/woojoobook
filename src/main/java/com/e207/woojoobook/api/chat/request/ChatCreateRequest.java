package com.e207.woojoobook.api.chat.request;

import com.e207.woojoobook.domain.chat.Chat;
import com.e207.woojoobook.domain.chatroom.ChatRoom;
import com.e207.woojoobook.domain.user.User;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ChatCreateRequest(@NotNull Long chatRoomId, @NotNull Long senderId, @NotNull Long receiverId,
								@NotNull String content) {

	public Chat toEntity(ChatRoom chatRoom, Long userId) {
		return Chat.builder()
			.chatRoom(chatRoom)
			.userId(userId)
			.content(content)
			.build();
	}
}
