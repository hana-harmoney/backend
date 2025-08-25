package com.example.hanaharmonybackend.service.serviceImpl;

import com.example.hanaharmonybackend.domain.ChatMessage;
import com.example.hanaharmonybackend.domain.ChatRoom;
import com.example.hanaharmonybackend.domain.User;
import com.example.hanaharmonybackend.payload.code.ErrorStatus;
import com.example.hanaharmonybackend.payload.exception.CustomException;
import com.example.hanaharmonybackend.repository.ChatMessageRepository;
import com.example.hanaharmonybackend.repository.ChatRoomRepository;
import com.example.hanaharmonybackend.repository.UserRepository;
import com.example.hanaharmonybackend.service.ChatMessageService;
import com.example.hanaharmonybackend.web.dto.chatMessage.ChatMessageRequest;
import com.example.hanaharmonybackend.web.dto.chatMessage.ChatMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ChatMessageResponse saveMessage(ChatMessageRequest request) {
//        User sender = SecurityUtil.getCurrentMember();
        User sender = userRepository.findById(6L) // 테스트용 사용자
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        ChatRoom room = chatRoomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new CustomException(ErrorStatus.CHATROOM_NOT_FOUND));

        User receiver = room.getUser1().getId().equals(sender.getId())
                ? room.getUser2()
                : room.getUser1();

        ChatMessage chatMessage = new ChatMessage(
                request.getMessage(),
                request.getAmount(),
                room,
                sender,
                receiver
        );

        ChatMessage saved = chatMessageRepository.save(chatMessage);

        return ChatMessageResponse.from(saved);
    }
}