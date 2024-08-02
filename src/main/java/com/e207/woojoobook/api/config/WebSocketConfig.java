package com.e207.woojoobook.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.e207.woojoobook.api.chatroom.interceptor.StompInterceptor;

import lombok.RequiredArgsConstructor;

@Profile("prod")
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	private final StompInterceptor stompInterceptor;

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config
			.setApplicationDestinationPrefixes("/app")
			.enableStompBrokerRelay("/topic")
			.setRelayHost("rabbitmq")
			.setVirtualHost("/")
			.setRelayPort(61613)
			.setSystemLogin("admin")
			.setSystemPasscode("e207")
			.setClientLogin("guest")
			.setClientPasscode("e207");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws")
			.setAllowedOriginPatterns("*"); //TODO 구체적인 경로로 수정이 필요하다.
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(stompInterceptor);
	}
}
