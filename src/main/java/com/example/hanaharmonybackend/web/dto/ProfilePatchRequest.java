package com.example.hanaharmonybackend.web.dto;

import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Builder
public record ProfilePatchRequest(
        String nickname,
        String description,
        List<Long> categoryIds,
        String rawPassword,
        MultipartFile profileImg,
        List<MultipartFile> descImagesAdd,
        List<Long> descImagesDeleteIds,
        String descImagesReorder         // "15,9,27"
) {
    public static ProfilePatchRequest of(
            String nickname, String description, List<Long> categoryIds, String rawPassword,
            MultipartFile profileImg, List<MultipartFile> descImagesAdd,
            List<Long> descImagesDeleteIds, String descImagesReorder
    ) {
        return ProfilePatchRequest.builder()
                .nickname(nickname)
                .description(description)
                .categoryIds(categoryIds)
                .rawPassword(rawPassword)
                .profileImg(profileImg)
                .descImagesAdd(descImagesAdd)
                .descImagesDeleteIds(descImagesDeleteIds)
                .descImagesReorder(descImagesReorder)
                .build();
    }
}
