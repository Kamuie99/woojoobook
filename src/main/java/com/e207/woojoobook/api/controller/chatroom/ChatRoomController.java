package com.e207.woojoobook.api.controller.chatroom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.e207.woojoobook.api.controller.chatroom.request.ChatRoomRequest;
import com.e207.woojoobook.api.controller.chatroom.response.ChatRoomCheckResponse;
import com.e207.woojoobook.api.controller.chatroom.response.ChatRoomResponse;
import com.e207.woojoobook.api.service.chatroom.ChatRoomService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@RestController
@Slf4j
public class ChatRoomController {

	private final ChatRoomService chatRoomService;

	@PostMapping("/chatrooms")
	public ResponseEntity<ChatRoomResponse> create(@RequestBody ChatRoomRequest request) {
		ChatRoomResponse response = chatRoomService.create(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);

	}

	@GetMapping("/chatrooms/check")
	public ResponseEntity<ChatRoomCheckResponse> checkExist(@Param("senderId") Long senderId,
		@Param("receiverId") Long receiverId) {
		ChatRoomCheckResponse response = chatRoomService.checkExistByUserIds(senderId, receiverId);
		log.info("isExist: {}", response.isExist());
		return ResponseEntity.ok(response);
	}

	@GetMapping("/chatrooms/{senderId}/{receiverId}")
	public ResponseEntity<ChatRoomResponse> find(@PathVariable("senderId") Long senderId,
		@PathVariable("receiverId") Long receiverId) {
		ChatRoomResponse response = chatRoomService.findByUserIds(senderId, receiverId);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/chatrooms")
	public ResponseEntity<Page<ChatRoomResponse>> findRooms(@Param("userId") Long userId, Pageable pageable) {
		Page<ChatRoomResponse> response = chatRoomService.findPageByUserId(userId, pageable);
		return ResponseEntity.ok(response);
	}
}
