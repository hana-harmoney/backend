package com.example.hanaharmonybackend.config.handler;

import com.example.hanaharmonybackend.util.JwtTokenProvider;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.http.server.ServerHttpRequest;

import java.security.Principal;
import java.util.Map;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    private final JwtTokenProvider jwtTokenProvider;

    public CustomHandshakeHandler(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        String token = request.getURI().getQuery();
        if (token == null || !token.startsWith("token=")) return null;

        String jwt = token.substring(6); // token= 제거
        if (!jwtTokenProvider.validateToken(jwt)) return null;

        String loginId = jwtTokenProvider.parseLoginId(jwt);

        return () -> loginId; // Principal 구현
    }
}