package com.e207.woojoobook.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.e207.woojoobook.api.book.response.BookListResponse;
import com.e207.woojoobook.api.book.response.BookResponse;
import com.e207.woojoobook.api.book.response.naver.NaverBookApiResponse;
import com.e207.woojoobook.api.book.response.naver.NaverBookItem;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class NaverBookSearchClient implements BookSearchClient {

	private static final String CircuitBreakerName = "naver-api-client";

	private final RestClient restClient;
	private final String CLIENT_ID;
	private final String CLIENT_SECRET;

	private final BookSearchClient aladinBookSearchClient;

	public NaverBookSearchClient(BookSearchClient aladinBookSearchClient,
		@Value("${api.naver.baseUrl}") String baseUrl,
		@Value("${api.naver.timeout}") String timeout,
		@Value("${naver-client-key}") String clientId,
		@Value("${naver-client-secret}") String clientSecret) {
		this.restClient = createRestClient(baseUrl, Integer.valueOf(timeout));
		this.CLIENT_ID = clientId;
		this.CLIENT_SECRET = clientSecret;
		this.aladinBookSearchClient = aladinBookSearchClient;
	}

	@CircuitBreaker(name = CircuitBreakerName, fallbackMethod = "searchByKeywordFallback")
	@Override
	public BookListResponse findBookByKeyword(String keyword, Integer page, Integer size) {
		NaverBookApiResponse naverBookApiResponse = restClient.get()
			.uri(uriBuilder -> uriBuilder.path("/v1/search/book.json")
				.queryParam("query", keyword)
				.queryParam("start", page)
				.queryParam("display", size)
				.build())
			.headers(httpHeaders -> {
				httpHeaders.set("X-Naver-Client-Id", CLIENT_ID);
				httpHeaders.set("X-Naver-Client-Secret", CLIENT_SECRET);
			})
			.retrieve()
			.body(NaverBookApiResponse.class);
		log.info("[Naver api] findBookByKeyword");

		List<BookResponse> bookResponses = new ArrayList<>();
		Integer totalResult = 0;
		if (naverBookApiResponse != null && naverBookApiResponse.items() != null) {
			totalResult = naverBookApiResponse.total();
			for (NaverBookItem item : naverBookApiResponse.items()) {
				BookResponse bookResponse = item.toBookResponse();
				bookResponses.add(bookResponse);
			}
		}
		Integer maxPage = totalResult != 0 ? (totalResult + size - 1) / size : 0;
		return new BookListResponse(maxPage, bookResponses);
	}

	@CircuitBreaker(name = CircuitBreakerName, fallbackMethod = "searchByIsbnFallback")
	@Override
	public Optional<BookResponse> findBookByIsbn(String isbn) {
		NaverBookApiResponse naverBookApiResponse = restClient.get()
			.uri(uriBuilder -> uriBuilder.path("/v1/search/book_adv.json").queryParam("d_isbn", isbn).build())
			.headers(httpHeaders -> {
				httpHeaders.set("X-Naver-Client-Id", CLIENT_ID);
				httpHeaders.set("X-Naver-Client-Secret", CLIENT_SECRET);
			})
			.retrieve()
			.body(NaverBookApiResponse.class);

		log.info("[Naver api] findBookByIsbn");

		if (naverBookApiResponse != null && naverBookApiResponse.items() != null) {
			return naverBookApiResponse.items().stream().map(NaverBookItem::toBookResponse).findAny();
		} else {
			return Optional.empty();
		}
	}

	public BookListResponse searchByKeywordFallback(String keyword, Integer page, Integer size, Exception e) {
		log.error("[" + CircuitBreakerName + "] request fail(keyword: {}, page: {}, pageSize: {}): ", keyword, page,
			size, e);
		return this.fallbackByKeyword(aladinBookSearchClient, keyword, page, size);
	}

	public Optional<BookResponse> searchByIsbnFallback(String isbn, Exception e) {
		log.error("[" + CircuitBreakerName + "] request fail(isbn: {}): ", isbn, e);
		return this.fallbackByIsbn(aladinBookSearchClient, isbn);
	}

	private RestClient createRestClient(String baseUrl, Integer timeout) {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(timeout);
		factory.setReadTimeout(timeout);

		return RestClient.builder()
			.defaultHeader("Content-Type", "application/json")
			.baseUrl(baseUrl)
			.requestFactory(factory)
			.build();
	}
}
