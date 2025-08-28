package com.example.hanaharmonybackend.service;

import com.example.hanaharmonybackend.web.dto.chatMessage.*;

public interface ChatMessageService {
    ChatMessageResponse saveMessage(ChatMessageRequest request, String loginId);

    ChatMessageListResponse getMessagesByRoomId(Long roomId);

    ChatMessageTransferResponse chatTransferAccountToAccount(Long roomId, ChatMessageTransferRequest request);
}
