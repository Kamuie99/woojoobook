package com.e207.woojoobook.api.user.request;

public record PasswordUpdateRequest(String curPassword, String password, String passwordConfirm) {
}
