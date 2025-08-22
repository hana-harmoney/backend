package com.example.hanaharmonybackend.web.controller;

import com.example.hanaharmonybackend.payload.ApiResponse;
import com.example.hanaharmonybackend.service.ChatRoomService;
import com.example.hanaharmonybackend.web.dto.ChatRoomListResponse;
import com.example.hanaharmonybackend.web.dto.ChatRoomRequest;
import com.example.hanaharmonybackend.web.dto.ChatRoomCreateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Chat", description = "채팅 API")
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatRoomService chatRoomService;

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
}