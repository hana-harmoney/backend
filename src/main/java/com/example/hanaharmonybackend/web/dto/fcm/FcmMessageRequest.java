package com.example.hanaharmonybackend.web.dto.fcm;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// FCM 전송 Dto
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FcmMessageRequest {
    @Schema(description = "유저ID")
    private Long userId;

    @Schema(description = "메시지 제목")
    private String title;

    @Schema(description = "메시지 내용")
    private String body;
}