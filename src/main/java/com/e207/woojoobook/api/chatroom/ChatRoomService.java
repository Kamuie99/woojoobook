package com.e207.woojoobook.api.chatroom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.e207.woojoobook.api.chatroom.request.ChatRoomRequest;
import com.e207.woojoobook.api.chatroom.response.ChatRoomCheckResponse;
import com.e207.woojoobook.api.chatroom.response.ChatRoomResponse;
import com.e207.woojoobook.api.user.UserService;
import com.e207.woojoobook.domain.chatroom.ChatRoom;
import com.e207.woojoobook.domain.chatroom.ChatRoomRepository;
import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.global.exception.ErrorCode;
import com.e207.woojoobook.global.exception.ErrorException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class ChatRoomService {

	private final ChatRoomRepository chatRoomRepository;
	private final UserService userService;

	@Transactional
	public ChatRoomResponse create(ChatRoomRequest request) {
		User sender = userService.findDomainById(request.senderId());
		User receiver = userService.findDomainById(request.receiverId());
		checkIfUsersAreDifferent(sender, receiver);
		ChatRoom chatRoom = createChatRoom(sender, receiver);
		ChatRoom createdChatRoom = chatRoomRepository.save(chatRoom);
		return ChatRoomResponse.of(createdChatRoom);
	}

	@Transactional(readOnly = true)
	public ChatRoomCheckResponse checkExistByUserIds(Long senderId, Long receiverId) {
		User sender = userService.findDomainById(senderId);
		User receiver = userService.findDomainById(receiverId);
		Long count = chatRoomRepository.countBySenderAndReceiver(sender, receiver);
		return ChatRoomCheckResponse.of(count >= 1); // TODO <jhl221123> 동시성 문제 발생
	}

	@Transactional(readOnly = true)
	public ChatRoomResponse findByUserIds(Long senderId, Long receiverId) {
		User sender = userService.findDomainById(senderId);
		User receiver = userService.findDomainById(receiverId);
		ChatRoom chatRoom = findDomainBySenderAndReceiver(sender, receiver);
		return ChatRoomResponse.of(chatRoom);
	}

	@Transactional(readOnly = true)
	public Page<ChatRoomResponse> findPageByUserId(Long userId, Pageable pageable) {
		User user = userService.findDomainById(userId);
		return chatRoomRepository.findPageBySenderOrReceiver(user, pageable)
			.map(ChatRoomResponse::of);
	}

	@Transactional(readOnly = true)
	public ChatRoom findDomainById(Long id) {
		return chatRoomRepository.findById(id)
			.orElseThrow(() -> new ErrorException(ErrorCode.NotFound));
	}

	private ChatRoom createChatRoom(User sender, User receiver) {
		return ChatRoom.builder()
			.sender(sender)
			.receiver(receiver)
			.build();
	}

	private ChatRoom findDomainBySenderAndReceiver(User sender, User receiver) {
		return chatRoomRepository.findBySenderAndReceiverWithUsers(sender, receiver)
			.orElseThrow(() -> new ErrorException(ErrorCode.NotFound));
	}

	private void checkIfUsersAreDifferent(User sender, User receiver) {
		if (sender.getId() == receiver.getId()) {
			throw new ErrorException(ErrorCode.BadRequest);
		}
	}
}
