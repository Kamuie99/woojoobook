package com.e207.woojoobook.domain.chatroom;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.user.UserSlaveRepository;

@DataJpaTest
class ChatRoomRepositoryTest {

	@Autowired
	private ChatRoomRepository chatRoomRepository;

	@Autowired
	private UserSlaveRepository userSlaveRepository;

	@DisplayName("수신자와 발신자가 참여 중인 채팅룸을 조회한다.")
	@Test
	void findBySenderAndReceiverSuccess() {
		//given
		User sender = createUser("sender");
		User receiver = createUser("receiver");
		User anotherUser = createUser("another user");
		userSlaveRepository.save(sender);
		userSlaveRepository.save(receiver);
		userSlaveRepository.save(anotherUser);

		ChatRoom chatRoom1 = createChatRoom(sender, receiver);
		ChatRoom chatRoom2 = createChatRoom(sender, anotherUser);
		chatRoomRepository.save(chatRoom1);
		chatRoomRepository.save(chatRoom2);

		//when
		ChatRoom result = chatRoomRepository.findBySenderAndReceiver(sender, receiver).get();

		//then
		assertThat(result.getSender().getNickname()).isEqualTo("sender");
		assertThat(result.getReceiver().getNickname()).isEqualTo("receiver");
	}

	@DisplayName("수신자와 발신자의 채팅룸이 없다면 조회되지 않는다.")
	@Test
	void findBySenderAndReceiverFail() {
		//given
		User sender = createUser("sender");
		User receiver = createUser("receiver");
		userSlaveRepository.save(sender);
		userSlaveRepository.save(receiver);

		//when
		Optional<ChatRoom> result = chatRoomRepository.findBySenderAndReceiver(sender, receiver);

		//then
		assertThat(result).isEmpty();
	}

	@DisplayName("사용가 참여 중인 채팅룸 목록을 페이지로 조회한다.")
	@Test
	void findBySenderOrReceiverSuccess() {
		//given
		User target = createUser("target");
		User anotherUser1 = createUser("anotherUser1");
		User anotherUser2 = createUser("anotherUser2");
		userSlaveRepository.save(target);
		userSlaveRepository.save(anotherUser1);
		userSlaveRepository.save(anotherUser2);

		ChatRoom chatRoom1 = createChatRoom(target, anotherUser1);
		ChatRoom chatRoom2 = createChatRoom(anotherUser2, target);
		ChatRoom chatRoom3 = createChatRoom(anotherUser1, anotherUser2);
		chatRoomRepository.save(chatRoom1);
		chatRoomRepository.save(chatRoom2);
		chatRoomRepository.save(chatRoom3);

		//when
		Page<ChatRoom> result = chatRoomRepository.findPageBySenderOrReceiver(target, PageRequest.of(0, 10));

		//then
		assertThat(result.getContent()).hasSize(2)
			.extracting("id")
			.containsExactlyInAnyOrder(chatRoom1.getId(), chatRoom2.getId());
	}

	@DisplayName("사용자가 참여중인 채팅룸이 없다면 빈 페이지가 조회된다.")
	@Test
	void findBySenderOrReceiverFail() {
		//given
		User target = createUser("target");
		User anotherUser1 = createUser("anotherUser1");
		User anotherUser2 = createUser("anotherUser2");
		userSlaveRepository.save(target);
		userSlaveRepository.save(anotherUser1);
		userSlaveRepository.save(anotherUser2);

		ChatRoom chatRoom = createChatRoom(anotherUser1, anotherUser2);
		chatRoomRepository.save(chatRoom);

		//when
		Page<ChatRoom> result = chatRoomRepository.findPageBySenderOrReceiver(target, PageRequest.of(0, 10));

		//then
		assertThat(result.getContent()).isEmpty();
	}

	@DisplayName("수신자와 발신자가 참여 중인 채팅룸이 있는지 확인한다.")
	@Test
	void countBySenderAndReceiverSuccess() { // TODO: 2024-07-21 시나리오 테스트로 변경
		//given
		User sender = createUser("sender");
		User receiver = createUser("receiver");
		User another = createUser("another");
		userSlaveRepository.save(sender);
		userSlaveRepository.save(receiver);
		userSlaveRepository.save(another);

		ChatRoom chatRoom1 = createChatRoom(sender, receiver);
		ChatRoom chatRoom2 = createChatRoom(sender, another);
		ChatRoom chatRoom3 = createChatRoom(another, receiver);
		chatRoomRepository.save(chatRoom1);
		chatRoomRepository.save(chatRoom2);
		chatRoomRepository.save(chatRoom3);

		//when
		Long result1 = chatRoomRepository.countBySenderAndReceiver(sender, receiver);
		Long result2 = chatRoomRepository.countBySenderAndReceiver(receiver, sender);

		//then
		assertThat(result1).isEqualTo(1);
		assertThat(result2).isEqualTo(1);
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