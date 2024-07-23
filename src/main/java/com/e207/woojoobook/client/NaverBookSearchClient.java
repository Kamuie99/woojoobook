package com.e207.woojoobook.client;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.e207.woojoobook.api.book.response.BookListResponse;
import com.e207.woojoobook.api.book.response.BookResponse;
import com.e207.woojoobook.api.book.response.NaverBookApiResponse;
import com.e207.woojoobook.api.book.response.NaverBookItem;

@Component
public class NaverBookSearchClient implements BookSearchClient {
	@Value("${naver-client-key}")
	private String naver_client_id;

	@Value("${naver-client-secret}")
	private String naver_client_secret;

	public BookListResponse findBookByKeyword(String keyword, Integer page, Integer size) {
		RestClient restClient = RestClient.builder()
			.baseUrl("https://openapi.naver.com")
			.build();

		NaverBookApiResponse naverBookApiResponse = restClient.get()
			.uri(uriBuilder -> uriBuilder.path("/v1/search/book.json")
				.queryParam("query", keyword)
				.queryParam("start", page)
				.queryParam("display", size)
				.build())
			.headers(httpHeaders -> {
				httpHeaders.set("X-Naver-Client-Id", naver_client_id);
				httpHeaders.set("X-Naver-Client-Secret", naver_client_secret);
			})
			.retrieve()
			.body(NaverBookApiResponse.class);

		List<BookResponse> bookResponses = new ArrayList<>();
		if (naverBookApiResponse != null && naverBookApiResponse.items() != null) {
			for (NaverBookItem item : naverBookApiResponse.items()) {
				BookResponse bookResponse = new BookResponse(item.isbn(), item.title(), item.author(),
					item.publisher(), LocalDate.parse(item.pubdate(), DateTimeFormatter.ofPattern("yyyyMMdd")),
					item.image(), item.description());

				bookResponses.add(bookResponse);
			}
		}

		return new BookListResponse(bookResponses);
	}
}
