package com.e207.woojoobook.api.chat;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.e207.woojoobook.api.chat.request.ChatCreateRequest;
import com.e207.woojoobook.api.chat.response.ChatResponse;
import com.e207.woojoobook.api.chatroom.ChatRoomService;
import com.e207.woojoobook.api.user.UserService;
import com.e207.woojoobook.domain.chat.Chat;
import com.e207.woojoobook.domain.chat.ChatRepository;
import com.e207.woojoobook.domain.chatroom.ChatRoom;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ChatService {

	private final ChatRepository chatRepository;
	private final ChatRoomService chatRoomService;
	private final UserService userService;

	@Transactional
	public ChatResponse create(ChatCreateRequest request) {
		ChatRoom chatRoom = chatRoomService.findDomainById(request.chatRoomId());

		Chat chat = request.toEntity(chatRoom, request.senderId());
		Chat savedChat = chatRepository.save(chat);
		chatRoom.changeModifiedAt(LocalDateTime.now());
		return ChatResponse.of(savedChat);
	}

	public Page<ChatResponse> findPageByChatRoomId(Long chatRoomId, Pageable pageable) {
		ChatRoom chatRoom = chatRoomService.findDomainById(chatRoomId);
		return chatRepository.findPageByChatRoom(chatRoom, pageable)
			.map(ChatResponse::of);
	}
}
