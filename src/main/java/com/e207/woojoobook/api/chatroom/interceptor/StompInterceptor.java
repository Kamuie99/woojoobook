package com.e207.woojoobook.api.chatroom.interceptor;

import com.e207.woojoobook.api.chat.response.UserOnResponse;
import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.global.exception.ErrorCode;
import com.e207.woojoobook.global.exception.ErrorException;
import com.e207.woojoobook.global.helper.UserHelper;
import com.e207.woojoobook.global.security.SecurityUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.e207.woojoobook.global.security.jwt.JwtProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompInterceptor implements ChannelInterceptor {

    private final JwtProvider jwtProvider;
    private final UserHelper userHelper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (accessor.getCommand() == StompCommand.CONNECT) {
            String token = resolveToken(accessor.getFirstNativeHeader("Authorization"));
            if (token == null) {
                throw new ErrorException(ErrorCode.ForbiddenError);
            }
            validateJwtToken(token);
            log.info("연결이 되었습니다. 연결 정보 : {}", SecurityUtil.getCurrentUsername());
            User currentUser = this.userHelper.findCurrentUser();

            UserOnResponse dto = UserOnResponse.toDto(currentUser);
            accessor.getSessionAttributes().put("id", currentUser.getId());
            redisTemplate.opsForSet().add("area:" + currentUser.getAreaCode(), dto);
        }

        if(accessor.getCommand() == StompCommand.SUBSCRIBE) {
            log.info("구독을 요청했습니다. 구독 경로 : {}", accessor.getDestination());
        }

        if (accessor.getCommand() == StompCommand.DISCONNECT) {
            Long userId = (Long) accessor.getSessionAttributes().get("id");
            if (userId != null) {
                User user = this.userHelper.findById(userId);
                if (user != null) {
                    UserOnResponse dto = UserOnResponse.toDto(user);
                    redisTemplate.opsForSet().remove("area:" + user.getAreaCode(), dto);
                    log.info("연결이 해제되었습니다. 사용자 정보: {}", userId);
                } else {
                    log.warn("사용자를 찾을 수 없습니다. 사용자 ID: {}", userId);
                }
            } else {
                log.warn("세션 속성에 사용자 ID가 없습니다.");
            }
        }
        return message;
    }

    private String resolveToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private void validateJwtToken(String token) {
        if (StringUtils.hasText(token) && jwtProvider.validateToken(token)) {
            Authentication authentication = jwtProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("Security Context에 '{}' 인증 정보를 저장했습니다.", authentication.getName());
        }
    }
}
