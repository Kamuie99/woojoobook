package com.e207.woojoobook.client;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.e207.woojoobook.api.book.response.BookItem;
import com.e207.woojoobook.api.book.response.BookItems;
import com.e207.woojoobook.api.book.response.naver.NaverBookResponse;
import com.e207.woojoobook.global.exception.ErrorCode;
import com.e207.woojoobook.global.exception.ErrorException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class NaverBookSearchClient implements BookSearchClient {

	private static final String CircuitBreakerName = "naver-api-client";

	private final RestClient restClient;
	private final String clientId;
	private final String clientSecret;

	private final BookSearchClient aladinBookSearchClient;

	public NaverBookSearchClient(BookSearchClient aladinBookSearchClient,
		@Value("${api.naver.baseUrl}") String baseUrl,
		@Value("${api.naver.timeout}") String timeout,
		@Value("${naver-client-key}") String clientId,
		@Value("${naver-client-secret}") String clientSecret) {
		this.restClient = createRestClient(baseUrl, Integer.valueOf(timeout));
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.aladinBookSearchClient = aladinBookSearchClient;
	}

	@CircuitBreaker(name = CircuitBreakerName, fallbackMethod = "searchByKeywordFallback")
	@Override
	public BookItems findBookByKeyword(String keyword, Integer page, Integer size) {
		NaverBookResponse naverBookResponse = restClient.get()
			.uri(uriBuilder -> uriBuilder.path("/v1/search/book.json")
				.queryParam("query", keyword)
				.queryParam("start", page)
				.queryParam("display", size)
				.build())
			.headers(httpHeaders -> {
				httpHeaders.set("X-Naver-Client-Id", clientId);
				httpHeaders.set("X-Naver-Client-Secret", clientSecret);
			})
			.retrieve()
			.body(NaverBookResponse.class);

		log.info("[naver api] find by keyword");

		List<BookItem> bookItems = Objects.requireNonNull(naverBookResponse).getItems().stream()
			.map(NaverBookResponse.Item::toBookItem)
			.toList();

		return BookItems.of(naverBookResponse.getTotal(), size, bookItems);
	}

	@CircuitBreaker(name = CircuitBreakerName, fallbackMethod = "searchByIsbnFallback")
	@Override
	public Optional<BookItem> findBookByIsbn(String isbn) {
		NaverBookResponse naverBookResponse = restClient.get()
			.uri(uriBuilder -> uriBuilder.path("/v1/search/book_adv.json").queryParam("d_isbn", isbn).build())
			.headers(httpHeaders -> {
				httpHeaders.set("X-Naver-Client-Id", clientId);
				httpHeaders.set("X-Naver-Client-Secret", clientSecret);
			})
			.retrieve()
			.body(NaverBookResponse.class);

		log.info("[naver api] find by isbn");

		return Optional.ofNullable(
			Objects.requireNonNull(naverBookResponse)
				.getItems().stream()
				.findAny()
				.orElseThrow(() -> new ErrorException(ErrorCode.InvalidAccess, "존재하지 않는 isbn: " + isbn))
				.toBookItem()
		);
	}

	public BookItems searchByKeywordFallback(String keyword, Integer page, Integer size, Exception e) {
		log.error("[" + CircuitBreakerName + "] request fail(keyword: {}, page: {}, pageSize: {}): ", keyword, page,
			size, e);
		return this.fallbackByKeyword(aladinBookSearchClient, keyword, page, size);
	}

	public Optional<BookItem> searchByIsbnFallback(String isbn, Exception e) {
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
