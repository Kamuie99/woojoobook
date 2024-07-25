package com.e207.woojoobook.api.user.request;

import jakarta.validation.constraints.NotNull;

public record UserDeleteRequest (@NotNull String password){
}
