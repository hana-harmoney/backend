package com.example.hanaharmonybackend.web.dto;

import com.example.hanaharmonybackend.domain.ChatRoom;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatRoomCreateResponse {
    Long roomId;

    public static ChatRoomCreateResponse fromEntity(ChatRoom chatRoom) {
        return ChatRoomCreateResponse.builder()
                .roomId(chatRoom.getId())
                .build();
    }
}