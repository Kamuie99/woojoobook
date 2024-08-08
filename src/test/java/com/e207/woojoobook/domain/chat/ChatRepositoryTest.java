package com.e207.woojoobook.domain.chat;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.e207.woojoobook.domain.chatroom.ChatRoom;
import com.e207.woojoobook.domain.chatroom.ChatRoomRepository;
import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.user.UserRepository;

@DataJpaTest
class ChatRepositoryTest {

	@Autowired
	private ChatRepository chatRepository;

	@Autowired
	private ChatRoomRepository chatRoomRepository;

	@Autowired
	private UserRepository userRepository;

	@DisplayName("채팅룸에 존재하는 채팅을 페이지로 조회한다.")
	@Test
	void findAllByChatRoomSuccess() {
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
		Page<Chat> result = chatRepository.findPageByChatRoom(chatRoom, PageRequest.of(0, 10));

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

	private ChatRoom createChatRoom(User sender, User receiver) {
		return ChatRoom.builder()
			.sender(sender)
			.receiver(receiver)
			.build();
	}

	private User createUser(String nickname) {
		return User.builder()
			.email("test@email.com")
			.password("encrypted password")
			.nickname(nickname)
			.areaCode("1234567")
			.build();
	}
}