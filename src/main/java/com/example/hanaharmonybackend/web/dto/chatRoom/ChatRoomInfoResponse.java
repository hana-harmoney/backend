package com.example.hanaharmonybackend.web.dto.chatRoom;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ChatRoomInfoResponse {
    private Long roomId;
    private String nickname;
    private String profileImageUrl;
    private LocalDateTime lastMessageTime;
    private String lastMessage;
}
