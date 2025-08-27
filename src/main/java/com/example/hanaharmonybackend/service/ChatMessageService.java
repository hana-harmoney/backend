package com.example.hanaharmonybackend.service;

import com.example.hanaharmonybackend.web.dto.chatMessage.ChatMessageListResponse;
import com.example.hanaharmonybackend.web.dto.chatMessage.ChatMessageRequest;
import com.example.hanaharmonybackend.web.dto.chatMessage.ChatMessageResponse;

public interface ChatMessageService {
    ChatMessageResponse saveMessage(ChatMessageRequest request, String loginId);

    ChatMessageListResponse getMessagesByRoomId(Long roomId);
}
