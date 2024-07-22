package com.e207.woojoobook.api.service.chatroom;

import static org.assertj.core.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import com.e207.woojoobook.api.controller.chatroom.request.ChatRoomRequest;
import com.e207.woojoobook.api.controller.chatroom.response.ChatRoomCheckResponse;
import com.e207.woojoobook.api.controller.chatroom.response.ChatRoomResponse;
import com.e207.woojoobook.domain.chat.ChatRepository;
import com.e207.woojoobook.domain.chatroom.ChatRoom;
import com.e207.woojoobook.domain.chatroom.ChatRoomRepository;
import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.user.UserSlaveRepository;
import com.e207.woojoobook.mock.TestConfig;

@SpringBootTest
@Import(TestConfig.class)
class ChatRoomServiceTest {

	@Autowired
	private ChatRoomService chatRoomService;

	@Autowired
	private ChatRoomRepository chatRoomRepository;

	@Autowired
	private ChatRepository chatRepository;

	@Autowired
	private UserSlaveRepository userSlaveRepository;

	@AfterEach
	void tearDown() {
		chatRepository.deleteAllInBatch();
		chatRoomRepository.deleteAllInBatch();
		userSlaveRepository.deleteAllInBatch();
	}

	@DisplayName("수신자와 발신자가 참여하는 채팅룸을 생성한다.")
	@Test
	void createSuccess() {
		//given
		User sender = createUser("sender");
		User receiver = createUser("receiver");
		userSlaveRepository.save(sender);
		userSlaveRepository.save(receiver);

		ChatRoomRequest request = ChatRoomRequest.builder()
			.senderId(sender.getId())
			.receiverId(receiver.getId())
			.build();

		//when
		ChatRoomResponse result = chatRoomService.create(request);

		//then
		assertThat(result)
			.extracting("senderId", "receiverId")
			.containsExactlyInAnyOrder(sender.getId(), receiver.getId());
	}

	@DisplayName("수신자와 발신자가 참여중인 채팅룸이 존재하는 지 확인한다.")
	@Test
	void checkChatRoomExistSuccess() { // TODO: 2024-07-21 시나리오 테스트로 변경
		//given
		User sender = createUser("sender");
		User receiver = createUser("receiver");
		userSlaveRepository.save(sender);
		userSlaveRepository.save(receiver);

		ChatRoom chatRoom = createChatRoom(sender, receiver);
		chatRoomRepository.save(chatRoom);

		//when
		ChatRoomCheckResponse result1 = chatRoomService.checkExistByUserIds(sender.getId(), receiver.getId());
		ChatRoomCheckResponse result2 = chatRoomService.checkExistByUserIds(receiver.getId(), sender.getId());

		//then
		assertThat(result1.isExist()).isTrue();
		assertThat(result2.isExist()).isTrue();
	}

	@DisplayName("수신자와 발신자가 참여중인 채팅룸을 조회한다.")
	@Test
	void findByUserIdsSuccess() {
		//given
		User sender = createUser("sender");
		User receiver = createUser("receiver");
		userSlaveRepository.save(sender);
		userSlaveRepository.save(receiver);

		ChatRoom chatRoom = createChatRoom(sender, receiver);
		chatRoomRepository.save(chatRoom);

		//when
		ChatRoomResponse result = chatRoomService.findByUserIds(sender.getId(), receiver.getId());

		//then
		assertThat(result).extracting("senderId", "receiverId")
			.containsExactlyInAnyOrder(sender.getId(), receiver.getId());
	}

	@DisplayName("수신자와 발신자가 참여중인 채팅룸이 존재하지 않는다면 예외가 발생한다.")
	@Test
	void findByUserIdsFail() {
		//given
		User sender = createUser("sender");
		User receiver = createUser("receiver");
		userSlaveRepository.save(sender);
		userSlaveRepository.save(receiver);

		//expected
		Assertions.assertThatThrownBy(() -> chatRoomService.findByUserIds(sender.getId(), receiver.getId()))
			.isInstanceOf(
				RuntimeException.class);
	}

	@DisplayName("사용자가 참여중인 채팅룸 목록을 페이지로 조회한다.")
	@Test
	void findPageByUserIdSuccess() {
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
		Page<ChatRoomResponse> resultPage = chatRoomService.findPageByUserId(target.getId(), PageRequest.of(0, 10));

		//then
		assertThat(resultPage.getContent()).hasSize(2)
			.extracting("id")
			.containsExactlyInAnyOrder(chatRoom1.getId(), chatRoom2.getId());
	}

	@Transactional
	@DisplayName("id로 채팅룸을 조회한다.")
	@Test
	void findDomainByIdSuccess() {
		//given
		User sender = createUser("sender");
		User receiver = createUser("receiver");
		userSlaveRepository.save(sender);
		userSlaveRepository.save(receiver);

		ChatRoom chatRoom = createChatRoom(sender, receiver);
		chatRoomRepository.save(chatRoom);

		//when
		ChatRoom result = chatRoomService.findDomainById(chatRoom.getId());

		//then
		assertThat(result.getSender().getNickname()).isEqualTo("sender");
		assertThat(result.getReceiver().getNickname()).isEqualTo("receiver");
	}

	@DisplayName("id에 해당하는 채팅룸이 없다면 예외가 발생한다.")
	@Test
	void findDomainByIdFail() {
		//expected
		assertThatThrownBy(() -> chatRoomService.findDomainById(1L)).isInstanceOf(RuntimeException.class);
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