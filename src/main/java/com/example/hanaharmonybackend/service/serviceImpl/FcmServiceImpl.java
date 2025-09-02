package com.example.hanaharmonybackend.service.serviceImpl;

import com.example.hanaharmonybackend.domain.User;
import com.example.hanaharmonybackend.repository.UserRepository;
import com.example.hanaharmonybackend.service.AuthService;
import com.example.hanaharmonybackend.service.FcmService;
import com.example.hanaharmonybackend.util.SecurityUtil;
import com.example.hanaharmonybackend.web.dto.fcm.FcmMessageRequest;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmServiceImpl implements FcmService {
//    https://fcm.googleapis.com/v1/projects/hanaharmoney-c35b3/messages:send

    private final AuthService authService;
    private final UserRepository userRepository;

    @Override
    public String sendMessage(FcmMessageRequest request) {
        String userFirebaseToken = authService.findFirebaseTokenByUserId(request.getUserId());

        if (userFirebaseToken == null || userFirebaseToken.isBlank()) {
            return "FCM token not found for userId=" + request.getUserId();
        }

        // 메시지 구성
        Message message = Message.builder()
                .setToken(userFirebaseToken)
                .setNotification(Notification.builder()
                        .setTitle(request.getTitle())
                        .setBody(request.getBody())
                        .build())
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            return "Message sent successfully: " + response;
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
            return "Failed to send message: " + e.getMessage();
        }
    }

    @Override
    public void sendFcmToken(String fcmToken) {
        User user = SecurityUtil.getCurrentMember();
        user.updateFcmToken(fcmToken);
        userRepository.save(user);
    }
}