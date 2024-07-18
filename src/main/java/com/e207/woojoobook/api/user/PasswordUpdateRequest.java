package com.e207.woojoobook.api.user;

public record PasswordUpdateRequest (String curPassword, String password, String passwordConfirm) { }
