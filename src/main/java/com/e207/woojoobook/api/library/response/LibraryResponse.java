package com.e207.woojoobook.api.library.response;

import com.e207.woojoobook.domain.library.Library;

import lombok.Builder;

@Builder
public record LibraryResponse(Long id, String categoryName, String books, Long orderNumber) {
	public static LibraryResponse of(Library library) {
		return LibraryResponse.builder()
			.id(library.getId())
			.categoryName(library.getName())
			.books(library.getBookList())
			.orderNumber(library.getOrderNumber())
			.build();
	}
}