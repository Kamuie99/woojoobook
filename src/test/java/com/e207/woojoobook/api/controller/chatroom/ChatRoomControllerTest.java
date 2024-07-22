package com.e207.woojoobook.api.controller.chatroom;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.e207.woojoobook.api.controller.chatroom.request.ChatRoomRequest;
import com.e207.woojoobook.api.controller.chatroom.response.ChatRoomCheckResponse;
import com.e207.woojoobook.api.controller.chatroom.response.ChatRoomResponse;
import com.e207.woojoobook.api.service.chatroom.ChatRoomService;
import com.e207.woojoobook.global.security.SecurityConfig;

@WebMvcTest(
	controllers = ChatRoomController.class,
	excludeAutoConfiguration = SecurityAutoConfiguration.class,
	excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
)
class ChatRoomControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	ChatRoomService chatRoomService;

	@DisplayName("채팅룸을 생성한다.")
	@Test
	void createSuccess() throws Exception {
		//given
		ChatRoomRequest request = createChatRoomRequest();
		String requestJson = objectMapper.writeValueAsString(request);
		ChatRoomResponse response = createChatRoomResponse(1L, 1L, 2L);
		String responseJson = objectMapper.writeValueAsString(response);

		BDDMockito.given(chatRoomService.create(any(ChatRoomRequest.class))).willReturn(response);

		//expected
		mockMvc.perform(post("/chatrooms")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson)
			)
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(content().json(responseJson));
	}

	@DisplayName("채팅룸이 존재하는 지 확인한다.")
	@Test
	void checkExistSuccess() throws Exception {
		//given
		ChatRoomCheckResponse response = ChatRoomCheckResponse.of(Boolean.TRUE);
		String responseJson = objectMapper.writeValueAsString(response);

		BDDMockito.given(chatRoomService.checkExistByUserIds(eq(1L), eq(2L))).willReturn(response);

		//expected
		mockMvc.perform(get("/chatrooms/check")
				.contentType(MediaType.APPLICATION_JSON)
				.queryParam("senderId", "1")
				.queryParam("receiverId", "2")
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().json(responseJson));
	}

	@DisplayName("수신자와 발신자의 채팅룸을 조회한다.")
	@Test
	void findSuccess() throws Exception {
		//given
		ChatRoomResponse response = createChatRoomResponse(1L, 1L, 2L);
		String responseJson = objectMapper.writeValueAsString(response);

		BDDMockito.given(chatRoomService.findByUserIds(eq(1L), eq(2L))).willReturn(response);

		//expected
		mockMvc.perform(get("/chatrooms/{senderId}/{receiverId}", 1L, 2L)
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().json(responseJson));
	}

	@DisplayName("사용자의 채팅룸 목록을 페이지로 조회한다.")
	@Test
	void findRoomsSuccess() throws Exception {
		//given
		ChatRoomResponse response1 = createChatRoomResponse(1L, 1L, 2L);
		ChatRoomResponse response2 = createChatRoomResponse(2L, 3L, 1L);
		Page<ChatRoomResponse> responsePage = new PageImpl<>(List.of(response1, response2), PageRequest.of(0, 10), 10);
		String responseJson = objectMapper.writeValueAsString(responsePage);

		BDDMockito.given(chatRoomService.findPageByUserId(eq(1L), any(Pageable.class))).willReturn(responsePage);

		//expected
		mockMvc.perform(get("/chatrooms")
				.contentType(MediaType.APPLICATION_JSON)
				.queryParam("userId", "1")
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().json(responseJson));
	}

	private ChatRoomResponse createChatRoomResponse(long id, long senderId, long receiverId) {
		return ChatRoomResponse.builder()
			.id(id)
			.senderId(senderId)
			.receiverId(receiverId)
			.build();
	}

	private ChatRoomRequest createChatRoomRequest() {
		return ChatRoomRequest.builder()
			.senderId(1L)
			.receiverId(2L)
			.build();
	}
}