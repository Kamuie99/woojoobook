package com.e207.woojoobook.api.book.response;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class BookItems {

	private Integer maxPage;

	@Builder.Default
	private List<BookItem> bookItems = new ArrayList<>();

	public static BookItems of(int total, int size, List<BookItem> bookItems) {
		int maxPage = total != 0 ? (total + size - 1) / size : 0;
		return BookItems.builder()
			.maxPage(maxPage)
			.bookItems(bookItems)
			.build();
	}
}