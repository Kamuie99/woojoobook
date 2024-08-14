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
import com.e207.woojoobook.api.book.response.aladin.AladinBookResponse;
import com.e207.woojoobook.global.exception.ErrorCode;
import com.e207.woojoobook.global.exception.ErrorException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AladinBookSearchClient implements BookSearchClient {

	private static final String CircuitBreakerName = "aladin-api-client";
	private static final String RESPONSE_TYPE = "JS";
	private static final String API_VERSION = "20131101";

	private final RestClient restClient;
	private final String clientKey;

	public AladinBookSearchClient(@Value("${api.aladin.baseUrl}") String baseUrl,
		@Value("${api.aladin.timeout}") String timeout,
		@Value("${aladin-client-ttbkey}") String aladinClientKey) {
		restClient = createRestClient(baseUrl, Integer.valueOf(timeout));
		this.clientKey = aladinClientKey;
	}

	@CircuitBreaker(name = CircuitBreakerName, fallbackMethod = "findBookFallback")
	@Override
	public BookItems findBookByKeyword(String keyword, Integer page, Integer size) {
		var aladinBookResponse = restClient.get()
			.uri(uriBuilder -> uriBuilder.path("/ttb/api/ItemSearch.aspx")
				.queryParam("ttbkey", clientKey)
				.queryParam("Query", keyword)
				.queryParam("start", page)
				.queryParam("MaxResults", size)
				.queryParam("output", RESPONSE_TYPE)
				.queryParam("Version", API_VERSION)
				.build()
			)
			.retrieve()
			.body(AladinBookResponse.class);

		log.info("[aladin api] find by keyword");

		List<BookItem> bookItems = Objects.requireNonNull(aladinBookResponse).getItems().stream()
			.map(AladinBookResponse.Item::toBookItem)
			.toList();

		return BookItems.of(aladinBookResponse.getTotal(), size, bookItems);
	}

	@CircuitBreaker(name = CircuitBreakerName, fallbackMethod = "findBookFallback")
	@Override
	public Optional<BookItem> findBookByIsbn(String isbn) {
		AladinBookResponse aladinBookResponse = restClient.get()
			.uri(uriBuilder -> uriBuilder.path("/ttb/api/ItemLookUp.aspx")
				.queryParam("ttbkey", clientKey)
				.queryParam("ItemIdType", "ISBN13")
				.queryParam("ItemId", isbn)
				.queryParam("output", "JS")
				.queryParam("Version", "20131101")
				.build()
			)
			.retrieve()
			.body(AladinBookResponse.class);

		log.info("[aladin api] find by isbn");

		return Optional.ofNullable(
			Objects.requireNonNull(aladinBookResponse)
				.getItems().stream()
				.findAny()
				.orElseThrow(() -> new ErrorException(ErrorCode.InvalidAccess, "존재하지 않는 isbn: " + isbn))
				.toBookItem()
		);
	}

	public void findBookFallback(Exception e) {
		log.error("[" + CircuitBreakerName + "] request fail ex: ", e);
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
