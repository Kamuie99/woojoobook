package com.e207.woojoobook.api.chat;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import com.e207.woojoobook.api.chat.request.ChatCreateRequest;
import com.e207.woojoobook.api.chat.response.ChatResponse;
import com.e207.woojoobook.domain.chat.Chat;
import com.e207.woojoobook.domain.chat.ChatRepository;
import com.e207.woojoobook.domain.chatroom.ChatRoom;
import com.e207.woojoobook.domain.chatroom.ChatRoomRepository;
import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.user.UserRepository;

@SpringBootTest
class ChatServiceTest {

	@Autowired
	private ChatService chatService;

	@Autowired
	private ChatRepository chatRepository;

	@Autowired
	private ChatRoomRepository chatRoomRepository;

	@Autowired
	private UserRepository userRepository;

	@DisplayName("채팅 등록에 성공한다.")
	@Test
	void createSuccess() {
		// given
		User sender = createUser("sender");
		User receiver = createUser("receiver");
		userRepository.save(sender);
		userRepository.save(receiver);

		ChatRoom chatRoom = createChatRoom(sender, receiver);
		chatRoomRepository.save(chatRoom);

		ChatCreateRequest request = createChatRequest(chatRoom, sender, "chat content");

		// when
		ChatResponse result = chatService.create(request);

		//then
		Chat createdChat = chatRepository.findById(result.id()).get();
		assertThat(result).extracting("id", "chatRoomId", "userId", "content")
			.containsExactly(createdChat.getId(), chatRoom.getId(), sender.getId(), "chat content");

	}

	@Transactional
	@DisplayName("채팅 등록 시, 채팅룸 수정 시간이 최신화된다.")
	@Test
	void createWithChatroomUpdateSuccess() throws InterruptedException {
		// given
		User sender = createUser("sender");
		User receiver = createUser("receiver");
		userRepository.save(sender);
		userRepository.save(receiver);

		ChatRoom chatRoom = createChatRoom(sender, receiver);
		chatRoomRepository.save(chatRoom);
		LocalDateTime beforeDateTime = chatRoom.getModifiedAt();

		ChatCreateRequest request = createChatRequest(chatRoom, sender, "chat content");

		// when
		Thread.sleep(50);
		chatService.create(request);

		//then
		LocalDateTime afterDateTime = chatRoom.getModifiedAt();
		assertThat(afterDateTime).isAfter(beforeDateTime);
	}

	@DisplayName("채팅룸에 존재하는 채팅 목록을 페이지로 조회한다.")
	@Test
	void findPageByChatRoomIdSuccess() {
		//given
		User sender = createUser("sender");
		User receiver = createUser("receiver");
		userRepository.save(sender);
		userRepository.save(receiver);

		ChatRoom chatRoom = createChatRoom(sender, receiver);
		chatRoomRepository.save(chatRoom);

		Chat chat1 = createChat(chatRoom, sender.getId(), "sender to receiver");
		Chat chat2 = createChat(chatRoom, receiver.getId(), "receiver to sender");
		chatRepository.save(chat1);
		chatRepository.save(chat2);

		//when
		Page<ChatResponse> result = chatService.findPageByChatRoomId(chatRoom.getId(), PageRequest.of(0, 10));

		//then
		assertThat(result.getContent()).hasSize(2)
			.extracting("content")
			.containsExactlyInAnyOrder("sender to receiver", "receiver to sender");
	}

	private Chat createChat(ChatRoom chatRoom, Long userId, String content) {
		return Chat.builder()
			.chatRoom(chatRoom)
			.userId(userId)
			.content(content)
			.build();
	}

	private ChatCreateRequest createChatRequest(ChatRoom chatRoom, User sender, String content) {
		return ChatCreateRequest.builder()
			.chatRoomId(chatRoom.getId())
			.senderId(sender.getId())
			.content(content)
			.build();
	}

	private ChatRoom createChatRoom(User sender, User receiver) {
		return ChatRoom.builder()
			.sender(sender)
			.receiver(receiver)
			.build();
	}

	private User createUser(String nickname) {
		return User.builder()
			.email("sender@test.com")
			.password("password")
			.nickname(nickname)
			.areaCode("000")
			.build();
	}
}