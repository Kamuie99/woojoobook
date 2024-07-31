package com.e207.woojoobook.api.user.response;

import lombok.Builder;

@Builder
public record UserInfoResponse (Long id, String email, String nickname, String areaCode){
}
