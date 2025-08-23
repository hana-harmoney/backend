package com.example.hanaharmonybackend.web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardCreateRequest {
    private String title;
    private String content;
    private Long wage;
    private String address;
    private Double latitude;
    private Double longitude;
    private String imageUrl;
    private Long categoryId;
}
