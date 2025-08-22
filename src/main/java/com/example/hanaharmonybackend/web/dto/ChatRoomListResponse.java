package com.example.hanaharmonybackend.web.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ChatRoomListResponse {
    private List<ChatRoomInfoDto> chatRoomList;
}