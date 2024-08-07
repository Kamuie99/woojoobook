package com.e207.woojoobook.domain.chatroom;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.e207.woojoobook.domain.user.User;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

	@Query("select cr from ChatRoom cr"
		+ " join fetch User s on cr.sender.id = s.id"
		+ " join fetch User r on cr.receiver.id = r.id"
		+ " where (cr.sender = :sender and cr.receiver = :receiver)"
		+ " or (cr.sender = :receiver and cr.receiver = :sender)")
	Optional<ChatRoom> findBySenderAndReceiverWithUsers(@Param("sender") User sender, @Param("receiver") User receiver);

	@Query("select cr from ChatRoom cr where cr.sender = :user or cr.receiver = :user")
	Page<ChatRoom> findPageBySenderOrReceiver(@Param("user") User user, Pageable pageable);

	@Query("select count(cr) from ChatRoom cr"
		+ " where (cr.sender = :sender and cr.receiver = :receiver)"
		+ " or (cr.sender = :receiver and cr.receiver = :sender)")
	Long countBySenderAndReceiver(@Param("sender") User sender,
		@Param("receiver") User receiver); // TODO: 2024-07-21 count query -> one search
}
