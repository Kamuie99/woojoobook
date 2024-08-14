package com.e207.woojoobook.api.chat.event;


import com.e207.woojoobook.api.chat.response.UserOnResponse;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.e207.woojoobook.domain.user.User;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class ChatEventListener {

	private final RedisTemplate<String, Object> redisTemplate;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleNicknameChangeEvent(UpdateRedisUserChangeEvent event) {
		User user = event.user();
		UserOnResponse prevUserOnResponse = new UserOnResponse(event.user().getId(), event.prevNickname());
		UserOnResponse userOnResponse = UserOnResponse.toDto(event.user());
		redisTemplate.opsForSet().remove("area:" + event.prevAreaCode(), prevUserOnResponse);
		redisTemplate.opsForSet().add("area:" + user.getAreaCode(), userOnResponse);
	}

	@EventListener
	public void handleUserDeleteEvent(UserDeleteRedisEvent event) {
		UserOnResponse userOnResponse = new UserOnResponse(event.id(), event.nickname());
		redisTemplate.opsForSet().remove("area:" + event.areaCode(), userOnResponse);
	}
}
