package com.example.hanaharmonybackend.web.dto.chatMessage;

import com.example.hanaharmonybackend.domain.ChatMessage;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatMessageResponse {
    private Long messageId;
    private Long roomId;
    private Long senderId;
    private Long receiverId;
    private String message;
    private Long amount;
    private LocalDateTime regdate;

    public static ChatMessageResponse from(ChatMessage chatMessage) {
        return ChatMessageResponse.builder()
                .messageId(chatMessage.getId())
                .roomId(chatMessage.getRoom().getId())
                .senderId(chatMessage.getSender().getId())
                .receiverId(chatMessage.getReceiver().getId())
                .message(chatMessage.getMessage())
                .amount(chatMessage.getAmount())
                .regdate(chatMessage.getCreatedAt())
                .build();
    }
}