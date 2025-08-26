package com.example.hanaharmonybackend.web.controller;

import com.example.hanaharmonybackend.payload.ApiResponse;
import com.example.hanaharmonybackend.service.ChatMessageService;
import com.example.hanaharmonybackend.service.ChatRoomService;
import com.example.hanaharmonybackend.web.dto.chatMessage.ChatMessageListResponse;
import com.example.hanaharmonybackend.web.dto.chatMessage.ChatMessageRequest;
import com.example.hanaharmonybackend.web.dto.chatMessage.ChatMessageResponse;
import com.example.hanaharmonybackend.web.dto.chatRoom.ChatRoomDetailResponse;
import com.example.hanaharmonybackend.web.dto.chatRoom.ChatRoomListResponse;
import com.example.hanaharmonybackend.web.dto.chatRoom.ChatRoomRequest;
import com.example.hanaharmonybackend.web.dto.chatRoom.ChatRoomCreateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Tag(name = "Chat", description = "채팅 API")
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatRoomService chatRoomService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;

    @Operation(summary = "채팅방 생성", description = "새로운 채팅방을 개설합니다.")
    @PostMapping("")
    public ApiResponse<ChatRoomCreateResponse> createChatRoom(@RequestBody ChatRoomRequest chatRoomRequest) {
        return ApiResponse.success(chatRoomService.createChatRoom(chatRoomRequest));
    }

    @Operation(summary = "채팅방 목록 조회", description = "유저가 참여한 채팅방리스트를 조회합니다.")
    @GetMapping("")
    public ApiResponse<ChatRoomListResponse> getChatRoomList() {
        return ApiResponse.success(chatRoomService.getChatRoomList());
    }

    @Operation(summary = "채팅방 단건 조회", description = "채팅방을 단건 조회합니다.")
    @GetMapping("/{roomId}")
    public ApiResponse<ChatRoomDetailResponse> getChatRoomDetail(@PathVariable Long roomId) {
        return ApiResponse.success(chatRoomService.getChatRoomDetail(roomId));
    }

    @Operation(summary = "채팅방 메시지 리스트 조회", description = "채팅방의 모든 메시지를 조회합니다.")
    @GetMapping("/{roomId}/message")
    public ApiResponse<ChatMessageListResponse> getMessages(@PathVariable Long roomId) {
        return ApiResponse.success(chatMessageService.getMessagesByRoomId(roomId));
    }

    @Operation(summary = "채팅 메세지 전송", description = "채팅 메세지를 전송합니다.")
    @MessageMapping("/chat/message")
    public void sendMessage(ChatMessageRequest request, Principal principal) {
        String loginId = principal.getName();
        ChatMessageResponse saved = chatMessageService.saveMessage(request, loginId);

        messagingTemplate.convertAndSend(
                "/sub/chat/room/" + request.getRoomId(),
                saved
        );
    }
}