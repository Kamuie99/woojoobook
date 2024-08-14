package com.e207.woojoobook.api.chat.event;

import com.e207.woojoobook.domain.user.User;

public record UserDeleteRedisEvent (Long id, String nickname, String areaCode){
}
