package com.example.hanaharmonybackend.web.dto.chatRoom;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatRoomReviewResponse {
    Long reviewedUserId;
    Double score;
    Double trust;
}
