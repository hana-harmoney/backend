package com.example.hanaharmonybackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 클라이언트가 메세지 받을 prefix
        registry.enableSimpleBroker("/sub");
        // 클라이언트가 메세지 보낼 prefix
        registry.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-stomp") // 웹소켓 연결 엔드포인트
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
