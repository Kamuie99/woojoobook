package com.e207.woojoobook.domain.chat;

import java.time.LocalDateTime;

import com.e207.woojoobook.domain.chatroom.ChatRoom;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Chat {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private ChatRoom chatRoom;

	private Long userId;

	private String content;

	private LocalDateTime createdAt;

	@Builder
	private Chat(Long id, ChatRoom chatRoom, Long userId, String content) {
		this.id = id;
		this.chatRoom = chatRoom;
		this.userId = userId;
		this.content = content;
		this.createdAt = LocalDateTime.now();
	}
}
