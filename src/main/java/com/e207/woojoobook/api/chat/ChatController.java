package com.e207.woojoobook.api.chat;

import static org.springframework.data.domain.Sort.Direction.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.e207.woojoobook.api.chat.request.ChatCreateRequest;
import com.e207.woojoobook.api.chat.response.ChatResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@RestController
public class ChatController {

	private final ChatService chatService;
	private final SimpMessageSendingOperations messageOperations;

	@GetMapping("/chat/{chatRoomId}")
	public ResponseEntity<Page<ChatResponse>> findPageByChatRoomId(@PathVariable("chatRoomId") Long chatRoomId,
		@PageableDefault(sort = "createdAt", direction = DESC) Pageable pageable) {
		Page<ChatResponse> chatResponsePage = chatService.findPageByChatRoomId(chatRoomId, pageable);
		log.info("chatResponsePage: {}", chatResponsePage.getContent());
		return ResponseEntity.ok(chatResponsePage);
	}

	@MessageMapping("/chat")
	public void createChatMessage(@Valid ChatCreateRequest request) {
		Long senderId = request.senderId();
		Long receiverId = request.receiverId();
		ChatResponse chatResponse = chatService.create(request);
		log.info("ChatController.chatResponse: {}", chatResponse);
		messageOperations.convertAndSend("/topic/user_" + senderId, chatResponse);
		messageOperations.convertAndSend("/topic/user_" + receiverId, chatResponse);
	}
}
