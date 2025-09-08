package com.example.hanaharmonybackend.config.handler;

import com.example.hanaharmonybackend.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand cmd = accessor.getCommand();
        System.out.println("넣으실 줄 알았다. ❤️ heart");
        log.info("로그 출력 테스트");
        log.debug("로그 시작!!");

        if (cmd != null) {
            System.out.println("[STOMP] cmd={}, nativeHeaders={}" + cmd + accessor.toNativeHeaderMap());
            log.debug("[STOMP] cmd={}, nativeHeaders={}", cmd, accessor.toNativeHeaderMap());
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String auth = getFirstNativeHeaderIgnoreCase(accessor, "Authorization");
            String token = (auth != null && auth.startsWith("Bearer ")) ? auth.substring(7) : null;
            if(token == null || token.length() == 0){
                System.out.println("[STOMP] token is null");
            }

            if (token == null || token.isBlank()) {
                throw new AccessDeniedException("Missing JWT for CONNECT");
            }

            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new AccessDeniedException("Invalid JWT");
            }

            accessor.setUser(authentication);
            // 주입 직후 즉시 확인
            System.out.println("[STOMP] CONNECT setUser -> {}"+ accessor.getUser());
            log.debug("[STOMP] CONNECT setUser -> {}", accessor.getUser());
            // 스프링 헤더에도 반영됐는지 확인
            log.debug("[STOMP] USER_HEADER in headers -> {}",
                accessor.getMessageHeaders().get("simpUser"));

            return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
        }

        log.debug("@@@@@@@@@@@@@end");


        // 인증이 필요한 프레임에서만 Principal 강제
        if ((StompCommand.SEND.equals(cmd) || StompCommand.SUBSCRIBE.equals(cmd) || StompCommand.UNSUBSCRIBE.equals(cmd))
            && accessor.getUser() == null) {
            throw new AccessDeniedException("No Principal found");
        }

        return message;
    }

    private String getFirstNativeHeaderIgnoreCase(StompHeaderAccessor accessor, String name) {
        Map<String, List<String>> map = accessor.toNativeHeaderMap();
        if (map == null) return null;
        for (Map.Entry<String, List<String>> e : map.entrySet()) {
            if (e.getKey() != null && e.getKey().equalsIgnoreCase(name)) {
                List<String> v = e.getValue();
                return (v != null && !v.isEmpty()) ? v.get(0) : null;
            }
        }
        return null;
    }
}