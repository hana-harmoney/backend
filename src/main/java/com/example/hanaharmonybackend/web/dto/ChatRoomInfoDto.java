package com.example.hanaharmonybackend.web.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatRoomInfoDto {
    private String nickname;
    private String profileImageUrl;
    private LocalDateTime lastMessageTime;
    private String lastMessage;
}
