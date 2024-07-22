package com.e207.woojoobook.api.controller.user.response;

import lombok.Builder;

@Builder
public record UserResponse(Long id, String nickname, String email) {
}
