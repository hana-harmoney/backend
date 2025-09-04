package com.example.hanaharmonybackend.web.dto.fcm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// FCM 전송 Dto
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FcmMessageRequest {
    private Long userId;
    private String title;
    private String body;
    private String image;
}