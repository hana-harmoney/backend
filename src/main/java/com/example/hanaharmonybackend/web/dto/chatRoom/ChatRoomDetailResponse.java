package com.example.hanaharmonybackend.web.dto.chatRoom;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatRoomDetailResponse {
    Long boardId;
    Long writerId;
    String nickname;
    String profileUrl;
    String title;
    Long wage;
    String address;
}
