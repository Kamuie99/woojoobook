package com.e207.woojoobook.api.chat.response;


import java.io.Serializable;

import com.e207.woojoobook.domain.user.User;

public record UserOnResponse (Long id, String nickname) implements Serializable {
    public static UserOnResponse toDto(User user) {
        return new UserOnResponse(user.getId(), user.getNickname());
    }
}
