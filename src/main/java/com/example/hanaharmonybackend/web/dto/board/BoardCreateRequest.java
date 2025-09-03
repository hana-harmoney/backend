package com.example.hanaharmonybackend.web.dto.board;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.media.Schema;

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

    @Schema(description = "이미지 파일", type = "string", format = "binary", nullable = true)
    private MultipartFile image;
}
