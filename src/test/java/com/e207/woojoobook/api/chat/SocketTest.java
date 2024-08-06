package com.e207.woojoobook.api.chat;

import com.e207.woojoobook.api.chat.response.UserOnResponse;
import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.user.UserRepository;
import com.e207.woojoobook.global.security.jwt.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.io.IOException;
import java.net.URI;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SocketTest {

    @LocalServerPort
    private int port;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private WebSocketStompClient stompClient;

    private String jwt;
    private String areaCode = "12345678";
    private User user;

    @BeforeEach
    void setup() throws IOException {
        // Stomp Client 설정
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
        ObjectMapper objectMapper = messageConverter.getObjectMapper();
        objectMapper.registerModules(new JavaTimeModule(), new ParameterNamesModule());
        stompClient.setMessageConverter(messageConverter);

        user = this.userRepository.save(User.builder()
                .email("test@test.com")
                .password(passwordEncoder.encode("12345678"))
                .areaCode(areaCode)
                .nickname("testAccount")
                .build());
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getId(), null);
        jwt = jwtProvider.createToken(token);
    }

    @AfterEach
    void tearDown() throws IOException {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
        if (stompClient != null) {
            stompClient.stop();
        }
    }

    @DisplayName("소켓 연결 시 JWT 토큰이 유효하다면 정상적으로 연결된다")
    @Test
    void connectWebSocketWithValidJwtToken_success() throws Exception {
        // given
        StompHeaders headers = new StompHeaders();
        headers.add("Authorization", "Bearer " + jwt);
        URI uri = new URI(String.format("ws://localhost:%d/ws", port));

        // expected
        assertDoesNotThrow(() -> {
            this.stompClient.connectAsync(uri, null, headers, new StompSessionHandlerAdapter() {
            }).get(5, TimeUnit.SECONDS );
        });
    }

    @DisplayName("소켓 연결 시 JWT 토큰이 유효하지 않다면 연결이 실패한다")
    @Test
    void connectWebSocketWithInvalidJwtToken_fail() throws Exception {
        // given
        StompHeaders headers = new StompHeaders();
        String invalidToken = "invalidToken";
        headers.add("Authorization", invalidToken);
        URI uri = new URI(String.format("ws://localhost:%d/ws", port));

        // expected
        assertThrows(ExecutionException.class, () -> {
            this.stompClient.connectAsync(uri, null, headers, new StompSessionHandlerAdapter() {
            }).get(5, TimeUnit.SECONDS );
        });
    }

    @DisplayName("소켓 연결 시 레디스에 지역별 접속 목록에 사용자가 저장된다")
    @Test
    void addAreaCodeSetInRedisWhenConnect() throws Exception {
        // given
        StompHeaders headers = new StompHeaders();
        headers.add("Authorization", "Bearer " + jwt);
        URI uri = new URI(String.format("ws://localhost:%d/ws", port));

        // when
        this.stompClient.connectAsync(uri, null, headers, new StompSessionHandlerAdapter() {
                }).get(5, TimeUnit.SECONDS);

        // then
        Set<Object> members = redisTemplate.opsForSet().members("area:" + areaCode);
        assertTrue(members.contains(UserOnResponse.toDto(user)));
    }

    @DisplayName("소켓 연결 해제 시 레디스에 사용자의 정보를 제거한다")
    @Test
    void removeAreaCodeSetInRedisWhenDisconnect() throws Exception {
        // given
        StompHeaders headers = new StompHeaders();
        headers.add("Authorization", "Bearer " + jwt);
        URI uri = new URI(String.format("ws://localhost:%d/ws", port));

        StompSession stompSession = this.stompClient.connectAsync(uri, null, headers, new StompSessionHandlerAdapter() {
        }).get(5, TimeUnit.SECONDS);

        Set<Object> members = redisTemplate.opsForSet().members("area:" + areaCode);
        assertTrue(members.contains(UserOnResponse.toDto(user)));

        CountDownLatch latch = new CountDownLatch(1);

        // when
        stompSession.disconnect();
        latch.await(100, TimeUnit.MILLISECONDS);
        latch.countDown();

        // then
        members = redisTemplate.opsForSet().members("area:" + areaCode);
        assertFalse(members.contains(UserOnResponse.toDto(user)));
    }
}
