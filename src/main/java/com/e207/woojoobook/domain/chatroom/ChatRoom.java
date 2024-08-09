package com.e207.woojoobook.domain.chatroom;

import java.time.LocalDateTime;

import com.e207.woojoobook.domain.user.User;

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
public class ChatRoom {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private User sender;

	@ManyToOne(fetch = FetchType.LAZY)
	private User receiver;

	private LocalDateTime modifiedAt;

	@Builder
	private ChatRoom(Long id, User sender, User receiver) {
		this.id = id;
		this.sender = sender;
		this.receiver = receiver;
		this.modifiedAt = LocalDateTime.now();
	}

	public void changeModifiedAt(LocalDateTime modifiedAt) {
		this.modifiedAt = modifiedAt;
	}

	public void removeSender(User user) {
		this.sender = user;
	}

	public void removeReceiver(User user) {
		this.receiver = user;
	}
}
