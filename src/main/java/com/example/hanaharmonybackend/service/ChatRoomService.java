package com.example.hanaharmonybackend.service;

import com.example.hanaharmonybackend.web.dto.chatMessage.ChatMessageResponse;
import com.example.hanaharmonybackend.web.dto.chatRoom.*;

import java.util.List;

public interface ChatRoomService {
    ChatRoomCreateResponse createChatRoom(ChatRoomRequest request);
    ChatRoomListResponse getChatRoomList();
    ChatRoomDetailResponse getChatRoomDetail(Long roomId);

    List<ChatRoomInfoResponse> getChatRoomsForUser(Long userId);
    List<ChatMessageResponse> getMessagesForRoom(Long roomId);
}