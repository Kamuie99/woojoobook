package com.e207.woojoobook.api.chatroom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.e207.woojoobook.api.chatroom.request.ChatRoomRequest;
import com.e207.woojoobook.api.chatroom.response.ChatRoomCheckResponse;
import com.e207.woojoobook.api.chatroom.response.ChatRoomResponse;
import com.e207.woojoobook.api.user.UserService;
import com.e207.woojoobook.domain.chatroom.ChatRoom;
import com.e207.woojoobook.domain.chatroom.ChatRoomRepository;
import com.e207.woojoobook.domain.user.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class ChatRoomService {

	private final ChatRoomRepository chatRoomRepository;
	private final UserService userService;

	public ChatRoomResponse create(ChatRoomRequest request) {
		User sender = userService.findDomainById(request.senderId());
		User receiver = userService.findDomainById(request.receiverId());
		ChatRoom chatRoom = createChatRoom(sender, receiver);
		ChatRoom createdChatRoom = chatRoomRepository.save(chatRoom);
		return ChatRoomResponse.of(createdChatRoom);
	}

	public ChatRoomCheckResponse checkExistByUserIds(Long senderId, Long receiverId) {
		User sender = userService.findDomainById(senderId);
		User receiver = userService.findDomainById(receiverId);
		Long count = chatRoomRepository.countBySenderAndReceiver(sender, receiver);
		log.info("room count: {}", count);
		return ChatRoomCheckResponse.of(count == 1);
	}

	public ChatRoomResponse findByUserIds(Long senderId, Long receiverId) {
		User sender = userService.findDomainById(senderId);
		User receiver = userService.findDomainById(receiverId);
		ChatRoom chatRoom = findDomainBySenderAndReceiver(sender, receiver);
		return ChatRoomResponse.of(chatRoom);
	}

	public Page<ChatRoomResponse> findPageByUserId(Long userId, Pageable pageable) {
		User user = userService.findDomainById(userId);
		return chatRoomRepository.findPageBySenderOrReceiver(user, pageable)
			.map(ChatRoomResponse::of);
	}

	public ChatRoom findDomainById(Long id) {
		return chatRoomRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("채팅방이 존재하지 않습니다."));
	}

	private ChatRoom createChatRoom(User sender, User receiver) {
		return ChatRoom.builder()
			.sender(sender)
			.receiver(receiver)
			.build();
	}

	private ChatRoom findDomainBySenderAndReceiver(User sender, User receiver) {
		return chatRoomRepository.findBySenderAndReceiver(sender, receiver)
			.orElseThrow(() -> new RuntimeException("채팅방이 존재하지 않습니다."));
	}
}
