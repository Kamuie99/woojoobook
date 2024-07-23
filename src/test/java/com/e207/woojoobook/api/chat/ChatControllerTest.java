package com.e207.woojoobook.api.chat;

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
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.test.web.servlet.MockMvc;

import com.e207.woojoobook.api.chat.response.ChatResponse;
import com.e207.woojoobook.global.security.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(
	controllers = ChatController.class,
	excludeAutoConfiguration = SecurityAutoConfiguration.class,
	excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
)
class ChatControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	ChatService chatService;

	@MockBean
	SimpMessageSendingOperations operations;

	@DisplayName("채팅룸에 있는 채팅 목록을 페이지로 조회한다.")
	@Test
	void findPageByChatRoomIdSuccess() throws Exception {
		//given
		ChatResponse response1 = createChatResponse(1L, 1L, "1 to 2");
		ChatResponse response2 = createChatResponse(1L, 2L, "2 to 1");
		Page<ChatResponse> responsePage = new PageImpl<>(List.of(response1, response2), PageRequest.of(0, 10), 10);
		String responseJson = objectMapper.writeValueAsString(responsePage);

		BDDMockito.given(chatService.findPageByChatRoomId(eq(1L), any(Pageable.class))).willReturn(responsePage);

		//expected
		mockMvc.perform(get("/chat/{chatRoomId}", 1)
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().json(responseJson));
	}

	private ChatResponse createChatResponse(long chatRoomId, long senderId, String content) {
		return ChatResponse.builder()
			.chatRoomId(chatRoomId)
			.senderId(senderId)
			.content(content)
			.build();
	}
}