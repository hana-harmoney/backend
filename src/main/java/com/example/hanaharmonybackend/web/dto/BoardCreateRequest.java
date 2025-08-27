package com.example.hanaharmonybackend.web.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class BoardCreateRequest {
    private String title;
    private String content;
    private Long wage;
    private String address;
    private Double latitude;
    private Double longitude;
    private Long categoryId;

    private MultipartFile image;

}
