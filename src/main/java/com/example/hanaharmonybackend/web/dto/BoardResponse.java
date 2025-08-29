package com.example.hanaharmonybackend.web.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BoardResponse {
    private Long boardId;
    private Long userId;
    private String nickname;
    private String phone;
    private Double trust;
    private String title;
    private String content;
    private Long wage;
    private String address;
    private Double latitude;
    private Double longitude;
    private String imageUrl;
    private String category;
    private boolean status;
    private String profileUrl;
    private boolean isMine;
    private Long chatRoomCnt;
    private Long chatRoomId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
