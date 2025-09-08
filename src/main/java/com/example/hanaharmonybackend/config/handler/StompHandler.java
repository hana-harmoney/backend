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

    // JWT 토큰을 마스킹해서 로그에 남기는 헬퍼
    private String maskToken(String token) {
        if (token == null) return null;
        int keep = Math.min(8, token.length());
        return token.substring(0, keep) + "...(" + token.length() + ")";
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand cmd = accessor.getCommand();
        log.info("@@@@@@@@@@ [WS] 수신명령={}, 세션={}", cmd, accessor.getSessionId());
        log.info("@@@@@@@@@@ [WS] NativeHeaders={}", accessor.toNativeHeaderMap());

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String auth = accessor.getFirstNativeHeader("Authorization"); // connectHeaders.Authorization
            log.info("@@@@@@@@@@ [WS] Authorization 헤더 존재={}, 값={}", auth != null, auth);
            String token = null;

            if (auth != null && auth.startsWith("Bearer ")) {
                token = auth.substring(7);
                log.info("@@@@@@@@@@ [WS] 토큰(헤더) 추출={}", maskToken(token));
            } else {
                // fallback: Handshake에서 세션 attribute로 저장해둔 토큰 사용(옵션)
                Map<String, Object> attrs = accessor.getSessionAttributes();
                Object wsToken = attrs != null ? attrs.get("wsToken") : null;
                log.info("@@@@@@@@@@ [WS] 세션 속성 존재={}, wsToken 존재={}", attrs != null, wsToken != null);
                if (wsToken instanceof String s && !s.isBlank()) token = s;
                log.info("@@@@@@@@@@ [WS] 토큰(세션) 추출={}", maskToken(token));
            }

            log.warn("[WS][차단] CONNECT 토큰 확인 단계 진입");
            if (token == null) {
                log.warn("@@@@@@@@@@ [WS][차단] CONNECT 토큰 없음");
                throw new AccessDeniedException("Missing JWT for STOMP CONNECT");
            }

            log.info("@@@@@@@@@@ [WS] JWT 검증 시작, 토큰={}", maskToken(token));
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("@@@@@@@@@@ [WS][차단] JWT 검증 실패 (null 또는 미인증)");
                throw new AccessDeniedException("Invalid JWT");
            } else {
                log.info("@@@@@@@@@@ [WS] JWT 검증 성공: {}", authentication.getName());
            }

            accessor.setUser(authentication); // ★ Principal 주입
            log.info("@@@@@@@@@@ [WS] Principal 설정 완료: {}", accessor.getUser() != null ? accessor.getUser().getName() : null);
            log.info("@@@@@@@@@@ [WS] CONNECT 완료: {}", authentication.getName());
            log.info("@@@@@@@@@@ [WS] Authorization 최종값={}", auth);
        }

        if (!StompCommand.CONNECT.equals(cmd)) {
            log.info("@@@@@@@@@@ [WS] 후속프레임: {} / Principal={}", cmd, accessor.getUser());
        }

        Principal user = accessor.getUser();
        if (user == null) {
            log.warn("@@@@@@@@@@ [WS][차단] Principal 없음 - cmd={}, 세션={}, 헤더={}", cmd, accessor.getSessionId(), accessor.toNativeHeaderMap());
            throw new AccessDeniedException("No Principal found");
        }
        log.info("@@@@@@@@@@ [WS] 인증확인: user={}", user.getName());
        return message;
    }
}