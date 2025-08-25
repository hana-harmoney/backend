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
import com.example.hanaharmonybackend.service.ChatRoomService;
import com.example.hanaharmonybackend.util.SecurityUtil;
import com.example.hanaharmonybackend.web.dto.chatMessage.ChatMessageListResponse;
import com.example.hanaharmonybackend.web.dto.chatMessage.ChatMessageRequest;
import com.example.hanaharmonybackend.web.dto.chatMessage.ChatMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ChatRoomService chatRoomService;

    @Override
    @Transactional
    public ChatMessageResponse saveMessage(ChatMessageRequest request, String loginId) {
        User sender = userRepository.findByLoginId(loginId)
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

    @Override
    public ChatMessageListResponse getMessagesByRoomId(Long roomId) {
        User loginUser = SecurityUtil.getCurrentMember();
        chatRoomService.isMember(roomId, loginUser.getLoginId());

        List<ChatMessage> messages = chatMessageRepository.findAllByRoomIdOrderByCreatedAtAsc(roomId);

        List<ChatMessageResponse> messageList = messages.stream()
                .map(ChatMessageResponse::from)
                .collect(Collectors.toList());

        return ChatMessageListResponse.builder()
                .chatMessageList(messageList)
                .build();
    }
}