package com.e207.woojoobook.api.chat.response;

import com.e207.woojoobook.domain.chat.Chat;

import lombok.Builder;

@Builder
public record ChatResponse(Long id, Long chatRoomId, Long senderId, String content) {

	public static ChatResponse of(Chat chat) {
		return new ChatResponse(
			chat.getId(),
			chat.getChatRoom().getId(),
			chat.getSender().getId(),
			chat.getContent()
		);
	}
}
