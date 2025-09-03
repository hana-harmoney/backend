package com.example.hanaharmonybackend.web.dto.board;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class BoardUpdateRequest {
    private String title;
    private String content;
    private Long wage;
    private String address;
    private Double latitude;
    private Double longitude;
    private Long categoryId;

    @Schema(description = "새 이미지 파일", type = "string", format = "binary", nullable = true)
    private MultipartFile image;

    @Schema(description = "기존 이미지 삭제 여부", example = "false")
    private boolean deleteImage;
}
