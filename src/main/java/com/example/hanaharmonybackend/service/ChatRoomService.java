package com.example.hanaharmonybackend.service;

import com.example.hanaharmonybackend.web.dto.chatRoom.*;

public interface ChatRoomService {
    ChatRoomCreateResponse createChatRoom(ChatRoomRequest request);

    ChatRoomListResponse getChatRoomList();

    ChatRoomDetailResponse getChatRoomDetail(Long roomId);

    boolean isMember(Long roomId, String loginId);

    ChatRoomReportResponse reportChatRoom(Long roomId);

    ChatRoomReviewResponse reviewChatRoom(Long roomId, ChatRoomReviewRequest reviewRequest);
}