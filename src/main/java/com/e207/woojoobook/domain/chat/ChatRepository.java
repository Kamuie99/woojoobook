package com.e207.woojoobook.domain.chat;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.e207.woojoobook.domain.chatroom.ChatRoom;

public interface ChatRepository extends JpaRepository<Chat, Long> {

	Page<Chat> findPageByChatRoom(ChatRoom chatRoom, Pageable pageable);
}
