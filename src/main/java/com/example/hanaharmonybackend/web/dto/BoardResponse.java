package com.example.hanaharmonybackend.web.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BoardResponse {
    private Long boardId;
    private String nickname;
    private Double trust;
    private String title;
    private String content;
    private Long wage;
    private String address;
    private String imageUrl;
    private String category;
    private boolean status;
    private String profileUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
