package com.e207.woojoobook.api.controller.user.request;

public record PasswordUpdateRequest (String curPassword, String password, String passwordConfirm) { }
