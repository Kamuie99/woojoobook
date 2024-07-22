package com.e207.woojoobook.api.service.chat;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.e207.woojoobook.api.controller.chat.request.ChatCreateRequest;
import com.e207.woojoobook.api.controller.chat.response.ChatResponse;
import com.e207.woojoobook.api.service.chatroom.ChatRoomService;
import com.e207.woojoobook.api.service.user.UserService;
import com.e207.woojoobook.domain.chat.Chat;
import com.e207.woojoobook.domain.chat.ChatRepository;
import com.e207.woojoobook.domain.chatroom.ChatRoom;
import com.e207.woojoobook.domain.user.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ChatService {

	private final ChatRepository chatRepository;
	private final ChatRoomService chatRoomService;
	private final UserService userService;

	public ChatResponse create(ChatCreateRequest request) {
		ChatRoom chatRoom = chatRoomService.findDomainById(request.chatRoomId());
		User sender = userService.findDomainById(request.senderId());
		Chat chat = request.toEntity(chatRoom, sender);
		Chat savedChat = chatRepository.save(chat);
		return ChatResponse.of(savedChat);
	}

	public Page<ChatResponse> findPageByChatRoomId(Long chatRoomId, Pageable pageable) {
		ChatRoom chatRoom = chatRoomService.findDomainById(chatRoomId);
		return chatRepository.findPageByChatRoom(chatRoom, pageable)
			.map(ChatResponse::of);
	}
}
