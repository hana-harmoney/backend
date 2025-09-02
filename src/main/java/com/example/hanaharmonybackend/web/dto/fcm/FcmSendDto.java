package com.example.hanaharmonybackend.web.dto.fcm;

import lombok.*;

// 전달 받은 객체
@Getter
@ToString
@Builder
public class FcmSendDto {
    private String token;
    private String titile;
    private String body;
}
