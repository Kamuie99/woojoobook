package com.e207.woojoobook.client;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@AutoConfigureWireMock(port = 0)
@TestPropertySource(properties = {
	"api.naver.baseUrl=http://localhost:${wiremock.server.port}",
	"api.aladin.baseUrl=http://localhost:${wiremock.server.port}"
})
class NaverBookSearchClientTest {
	private static final int PAGE = 1;
	private static final int PAGE_SIZE = 5;

	@Autowired
	private NaverBookSearchClient sut;

	@Test
	@DisplayName("도서 조회 시, 네이버 api가 정상 응답한다.")
	public void success() {
		// given
		var keyword = "success";

		// when
		var actual = sut.findBookByKeyword(keyword, PAGE, PAGE_SIZE);

		// then
		assertThat(actual.maxPage()).isEqualTo(1);
		assertThat(actual.bookList()).hasSize(5);
	}

	@Test
	@DisplayName("응답 지연으로 네이버 api 대신 알라딘 api가 사용된다.")
	public void fail() {
		// given
		var keyword = "fail";

		// when
		var actual = sut.findBookByKeyword(keyword, PAGE, PAGE_SIZE);

		// then
		assertThat(actual.maxPage()).isEqualTo(1);
		assertThat(actual.bookList()).hasSize(5);
	}
}