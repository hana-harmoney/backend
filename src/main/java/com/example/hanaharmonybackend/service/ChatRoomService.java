package com.example.hanaharmonybackend.service;

import com.example.hanaharmonybackend.web.dto.ChatRoomDetailResponse;
import com.example.hanaharmonybackend.web.dto.ChatRoomListResponse;
import com.example.hanaharmonybackend.web.dto.ChatRoomRequest;
import com.example.hanaharmonybackend.web.dto.ChatRoomCreateResponse;

public interface ChatRoomService {
    ChatRoomCreateResponse createChatRoom(ChatRoomRequest request);
    ChatRoomListResponse getChatRoomList();
    ChatRoomDetailResponse getChatRoomDetail(Long roomId);
}