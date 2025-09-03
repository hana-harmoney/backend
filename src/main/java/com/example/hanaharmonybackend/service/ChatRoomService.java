package com.example.hanaharmonybackend.service;

import com.example.hanaharmonybackend.domain.ChatRoom;
import com.example.hanaharmonybackend.web.dto.chatRoom.*;

public interface ChatRoomService {
    ChatRoomCreateResponse createChatRoom(ChatRoomRequest request);

    ChatRoomListResponse getChatRoomList();

    ChatRoomDetailResponse getChatRoomDetail(Long roomId);

    ChatRoomReportResponse reportChatRoom(Long roomId);

    ChatRoomReviewResponse reviewChatRoom(Long roomId, ChatRoomReviewRequest reviewRequest);

    ChatRoom getValidRoom(Long roomId, Long userId);
}