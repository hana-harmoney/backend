package com.example.hanaharmonybackend.web.controller;

import com.example.hanaharmonybackend.payload.ApiResponse;
import com.example.hanaharmonybackend.service.FcmService;
import com.example.hanaharmonybackend.web.dto.fcm.FcmMessageRequest;
import com.example.hanaharmonybackend.web.dto.fcm.FcmTokenRequest;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fcm")
@RequiredArgsConstructor
public class FcmController {
    private final FcmService fcmService;

    @Operation(summary = "FCM 푸시 알림", description = "FCM 푸시 알림을 전송합니다.")
    @PostMapping("/sendMessage")
    public ApiResponse<String> sendMessage(@RequestBody FcmMessageRequest request) {
        return ApiResponse.success(fcmService.sendMessage(request));
    }

    @Operation(summary = "DeviceToken 요청", description = "DeviceToken을 요청하고 저장합니다.")
    @PostMapping("/deviceToken")
    public ApiResponse<Void> requestToken(@RequestBody FcmTokenRequest request) {
        fcmService.sendFcmToken(request.getFcmToken());
        return ApiResponse.success(null);
    }
}