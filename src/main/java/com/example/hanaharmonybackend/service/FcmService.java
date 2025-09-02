package com.example.hanaharmonybackend.service;

import com.example.hanaharmonybackend.web.dto.fcm.FcmMessageRequest;

public interface FcmService {
    String sendMessage(FcmMessageRequest request);
    void sendFcmToken(String fcmToken);
}
