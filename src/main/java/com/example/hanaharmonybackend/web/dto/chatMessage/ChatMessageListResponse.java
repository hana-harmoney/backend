package com.example.hanaharmonybackend.web.dto.chatMessage;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ChatMessageListResponse {
    private List<ChatMessageResponse> chatMessageList;
}
