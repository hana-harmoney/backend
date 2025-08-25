package com.example.hanaharmonybackend.web.dto.chatRoom;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ChatRoomListResponse {
    private List<ChatRoomInfoResponse> chatRoomList;
}