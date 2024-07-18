package com.e207.woojoobook.global.security;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.e207.woojoobook.global.security.jwt.JwtAccessDeniedHandler;
import com.e207.woojoobook.global.security.jwt.JwtAuthenticationEntryPoint;
import com.e207.woojoobook.global.security.jwt.JwtAuthenticationFilter;
import com.e207.woojoobook.global.security.jwt.JwtProvider;

@Configuration
public class SecurityConfig {

	private static final String[] AUTH_WHITELIST = {
	};

	@Bean
	JwtProvider jwtProvider() {
		return new JwtProvider();
	}

	@Bean
	JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
		return new JwtAuthenticationEntryPoint();
	}

	@Bean
	JwtAccessDeniedHandler jwtAccessDeniedHandler() {
		return new JwtAccessDeniedHandler();
	}

	@Bean
	JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter(jwtProvider());
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web
			.ignoring()
			.requestMatchers(PathRequest.toStaticResources().atCommonLocations())
			.requestMatchers("/h2-console/**");
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		// 비동기 처리를 위한 securityContext 전략 설정
		SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);

		return http
			.httpBasic(AbstractHttpConfigurer::disable)
			.csrf(AbstractHttpConfigurer::disable)
			.headers(headers ->
				headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
			.formLogin(AbstractHttpConfigurer::disable)
			.exceptionHandling(handler ->
				handler.authenticationEntryPoint(jwtAuthenticationEntryPoint())
					.accessDeniedHandler(jwtAccessDeniedHandler())
			)
			.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
			.authorizeHttpRequests(request -> {
				request.requestMatchers(AUTH_WHITELIST).permitAll()
					.requestMatchers(HttpMethod.POST, "/users").permitAll()
					.requestMatchers(HttpMethod.GET, "/users/nicknames/**").permitAll()
					.requestMatchers("/users/emails/**").permitAll()
					.anyRequest().authenticated();
			})
			.sessionManagement(sessionManagement ->
				sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
}
