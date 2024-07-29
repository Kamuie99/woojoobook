package com.e207.woojoobook.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.e207.woojoobook.api.book.response.BookListResponse;
import com.e207.woojoobook.api.book.response.BookResponse;
import com.e207.woojoobook.api.book.response.NaverBookApiResponse;
import com.e207.woojoobook.api.book.response.NaverBookItem;

@Component
public class NaverBookSearchClient implements BookSearchClient {

	private final String NAVER_CLIENT_ID;
	private final String NAVER_CLIENT_SECRET;

	public NaverBookSearchClient(@Value("${naver-client-key}") String NAVER_CLIENT_ID,
		@Value("${naver-client-secret}") String NAVER_CLIENT_SECRET) {
		this.NAVER_CLIENT_ID = NAVER_CLIENT_ID;
		this.NAVER_CLIENT_SECRET = NAVER_CLIENT_SECRET;
	}

	public BookListResponse findBookByKeyword(String keyword, Integer page, Integer size) {
		RestClient restClient = createRestClient();
		NaverBookApiResponse naverBookApiResponse = restClient.get()
			.uri(uriBuilder -> uriBuilder.path("/v1/search/book.json")
				.queryParam("query", keyword)
				.queryParam("start", page)
				.queryParam("display", size)
				.build())
			.headers(httpHeaders -> {
				httpHeaders.set("X-Naver-Client-Id", NAVER_CLIENT_ID);
				httpHeaders.set("X-Naver-Client-Secret", NAVER_CLIENT_SECRET);
			})
			.retrieve()
			.body(NaverBookApiResponse.class);

		List<BookResponse> bookResponses = new ArrayList<>();
		Integer totalResult = 0;
		if (naverBookApiResponse != null && naverBookApiResponse.items() != null) {
			totalResult = naverBookApiResponse.total();
			for (NaverBookItem item : naverBookApiResponse.items()) {
				BookResponse bookResponse = item.toBookResponse();
				bookResponses.add(bookResponse);
			}
		}
		Integer maxPage = totalResult != 0 ? (totalResult / size) + 1 : 0;
		return new BookListResponse(maxPage, bookResponses);
	}

	@Override
	public Optional<BookResponse> findBookByIsbn(String isbn) {
		RestClient restClient = createRestClient();
		NaverBookApiResponse naverBookApiResponse = restClient.get()
			.uri(uriBuilder -> uriBuilder.path("/v1/search/book_adv.json").queryParam("d_isbn", isbn).build())
			.headers(httpHeaders -> {
				httpHeaders.set("X-Naver-Client-Id", NAVER_CLIENT_ID);
				httpHeaders.set("X-Naver-Client-Secret", NAVER_CLIENT_SECRET);
			})
			.retrieve()
			.body(NaverBookApiResponse.class);

		if (naverBookApiResponse != null && naverBookApiResponse.items() != null) {
			return naverBookApiResponse.items().stream().map(NaverBookItem::toBookResponse).findAny();
		} else {
			return Optional.empty();
		}
	}

	private RestClient createRestClient() {
		return RestClient.builder().baseUrl("https://openapi.naver.com").build();
	}
}
