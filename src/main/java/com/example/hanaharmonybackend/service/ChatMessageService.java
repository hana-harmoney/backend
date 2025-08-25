package com.example.hanaharmonybackend.service;

import com.example.hanaharmonybackend.web.dto.chatMessage.ChatMessageRequest;
import com.example.hanaharmonybackend.web.dto.chatMessage.ChatMessageResponse;

public interface ChatMessageService {
    ChatMessageResponse saveMessage(ChatMessageRequest request, String loginId);
}
