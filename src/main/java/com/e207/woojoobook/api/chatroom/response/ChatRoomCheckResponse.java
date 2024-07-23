package com.e207.woojoobook.api.chatroom.response;

import lombok.Builder;

@Builder
public record ChatRoomCheckResponse(Boolean isExist) {

	public static ChatRoomCheckResponse of(Boolean isExist) {
		return new ChatRoomCheckResponse(isExist);
	}
}
