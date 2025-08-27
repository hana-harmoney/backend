package com.example.hanaharmonybackend.config.handler;

import com.example.hanaharmonybackend.util.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.http.server.ServerHttpRequest;

import java.security.Principal;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    private final JwtTokenProvider jwtTokenProvider;

    public CustomHandshakeHandler(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected Principal determineUser(
            ServerHttpRequest request,
            org.springframework.web.socket.WebSocketHandler wsHandler,
            java.util.Map<String, Object> attributes) {

        String token = extractCookie(request, "access_token");
        if (token != null && jwtTokenProvider.validateToken(token)) {
            String userId = jwtTokenProvider.parseLoginId(token);

            return new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                    userId, null, java.util.List.of()
            );
        }
        // 토큰이 없거나 유효하지 않으면 거절하거나 익명 처리
        return null; // null이면 연결 거절 (403)
    }

    private String extractCookie(ServerHttpRequest request, String name) {
        if (request instanceof ServletServerHttpRequest servlet) {
            HttpServletRequest http = servlet.getServletRequest();
            Cookie[] cookies = http.getCookies();
            if (cookies != null) {
                for (Cookie c : cookies) {
                    if (name.equals(c.getName())) return c.getValue();
                }
            }
        }
        return null;
    }
}