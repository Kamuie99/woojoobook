package com.woojoobook.woojoobook.global.security.jwt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.e207.woojoobook.global.security.SecurityConfig;
import com.e207.woojoobook.global.security.jwt.JwtAuthenticationFilter;
import com.e207.woojoobook.global.security.jwt.JwtProvider;

@Import(SecurityConfig.class)
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

	@Mock
	JwtProvider jwtProvider;

	@InjectMocks
	JwtAuthenticationFilter jwtAuthenticationFilter;

	@Mock
	MockHttpServletRequest request;

	@Mock
	MockHttpServletResponse response;

	@Mock
	MockFilterChain filterChain;

	@BeforeEach
	void setUp() {
		SecurityContextHolder.clearContext();
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		filterChain = new MockFilterChain();
	}

	@Test
	@DisplayName("토큰이 유효할 경우 사용자 인증 성공")
	public void whenTokenIsValidThenAuthenticate() throws Exception {
		// given
		String token = "validToken";
		Authentication authentication = mock(Authentication.class);

		request.addHeader("Authorization", "Bearer " + token);

		when(jwtProvider.validateToken(token)).thenReturn(true);
		when(jwtProvider.getAuthentication(token)).thenReturn(authentication);

		// when
		jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

		// then
		assertNotNull(SecurityContextHolder.getContext().getAuthentication());
	}

	@Test
	@DisplayName("토큰이 유효하지 않을 경우 인증 실패")
	public void whenTokenIsInvalidThenAuthenticationFails() throws Exception {
		// given
		String token = "invalidToken";
		request.addHeader("Authorization", "Bearer " + token);
		when(jwtProvider.validateToken(token)).thenReturn(false);

		// when
		jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

		// then
		assertNull(SecurityContextHolder.getContext().getAuthentication());
	}

}