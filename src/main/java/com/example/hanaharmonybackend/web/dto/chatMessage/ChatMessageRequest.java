package com.example.hanaharmonybackend.web.dto.chatMessage;

import lombok.Data;

@Data
public class ChatMessageRequest {
    private Long roomId;
    private String message;
    private Long amount;
}