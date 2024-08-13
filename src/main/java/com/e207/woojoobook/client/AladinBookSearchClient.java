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
import com.e207.woojoobook.api.book.response.aladin.AladinBookApiResponse;
import com.e207.woojoobook.api.book.response.aladin.AladinBookItem;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AladinBookSearchClient implements BookSearchClient {

	private static final String CircuitBreakerName = "aladin-api-client";

	private final RestClient restClient;
	private final String CLIENT_TTBKEY;

	public AladinBookSearchClient(@Value("${api.aladin.baseUrl}") String baseUrl,
		@Value("${api.aladin.timeout}") String timeout,
		@Value("${aladin-client-ttbkey}") String aladinClientKey) {
		restClient = createRestClient(baseUrl, Integer.valueOf(timeout));
		this.CLIENT_TTBKEY = aladinClientKey;
	}

	@CircuitBreaker(name = CircuitBreakerName, fallbackMethod = "findBookFallback")
	@Override
	public BookListResponse findBookByKeyword(String keyword, Integer page, Integer size) {
		AladinBookApiResponse aladinBookApiResponse = restClient.get()
			.uri(uriBuilder -> uriBuilder.path("/ttb/api/ItemSearch.aspx")
				.queryParam("ttbkey", CLIENT_TTBKEY)
				.queryParam("Query", keyword)
				.queryParam("start", page)
				.queryParam("MaxResults", size)
				.queryParam("output", "JS")
				.queryParam("Version", "20131101")
				.build()
			)
			.retrieve()
			.body(AladinBookApiResponse.class);

		log.info("[Aladin api] findBookByKeyword");

		List<BookResponse> bookResponses = new ArrayList<>();
		Integer totalResult = 0;
		if (aladinBookApiResponse != null && aladinBookApiResponse.item() != null) {
			totalResult = aladinBookApiResponse.totalResults();
			for (AladinBookItem item : aladinBookApiResponse.item()) {
				BookResponse bookResponse = item.toBookResponse();
				bookResponses.add(bookResponse);
			}
		}
		Integer maxPage = totalResult != 0 ? (totalResult + size - 1) / size : 0;
		return new BookListResponse(maxPage, bookResponses);
	}

	@CircuitBreaker(name = CircuitBreakerName, fallbackMethod = "findBookFallback")
	@Override
	public Optional<BookResponse> findBookByIsbn(String isbn13) {
		AladinBookApiResponse aladinBookApiResponse = restClient.get()
			.uri(uriBuilder -> uriBuilder.path("/ttb/api/ItemLookUp.aspx")
				.queryParam("ttbkey", CLIENT_TTBKEY)
				.queryParam("ItemIdType", "ISBN13")
				.queryParam("ItemId", isbn13)
				.queryParam("output", "JS")
				.queryParam("Version", "20131101")
				.build()
			)
			.retrieve()
			.body(AladinBookApiResponse.class);

		log.info("[Aladin api] findBookByIsbn");

		if (aladinBookApiResponse != null && aladinBookApiResponse.item() != null) {
			return aladinBookApiResponse.item().stream()
				.map(AladinBookItem::toBookResponse)
				.findAny();
		} else {
			return Optional.empty();
		}
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
