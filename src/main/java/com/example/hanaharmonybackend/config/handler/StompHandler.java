package com.example.hanaharmonybackend.config.handler;

import com.example.hanaharmonybackend.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String auth = accessor.getFirstNativeHeader("Authorization"); // connectHeaders.Authorization
            String token = null;

            if (auth != null && auth.startsWith("Bearer ")) {
                token = auth.substring(7);
            } else {
                // fallback: Handshake에서 세션 attribute로 저장해둔 토큰 사용(옵션)
                Map<String, Object> attrs = accessor.getSessionAttributes();
                Object wsToken = attrs != null ? attrs.get("wsToken") : null;
                if (wsToken instanceof String s && !s.isBlank()) token = s;
            }

            if (token == null) {
                throw new AccessDeniedException("Missing JWT for STOMP CONNECT");
            }

            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            if (authentication == null || !authentication.isAuthenticated()) {
                System.out.println(authentication + "@@@@@@@@@@@@@@@@@@@@@");
                throw new AccessDeniedException("Invalid JWT");
            }

            accessor.setUser(authentication); // ★ Principal 주입
            log.info("[STOMP] CONNECT as {}", authentication.getName());
            log.info("CONNECT with Authorization header = {}", auth);
        }

        Principal user = accessor.getUser();
        if (user == null) {
            throw new AccessDeniedException("No Principal found");
        }
        log.info("STOMP Message Authenticated for user: {}", user.getName());
        return message;
    }
}