package com.e207.woojoobook.api.chat.event;

import com.e207.woojoobook.domain.user.User;

public record UpdateRedisUserChangeEvent(User user, String prevNickname, String prevAreaCode) {
}
