package com.e207.woojoobook.api.library.response;

import java.util.List;

import lombok.Builder;

@Builder
public record LibraryListResponse(String nickName, List<LibraryResponse> libraryList) {
}