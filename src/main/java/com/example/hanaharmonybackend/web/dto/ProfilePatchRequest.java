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
        List<Long> descImagesDeleteIds
) {
    public static ProfilePatchRequest of(
            String nickname, String description, List<Long> categoryIds, String rawPassword,
            MultipartFile profileImg, List<MultipartFile> descImagesAdd,
            List<Long> descImagesDeleteIds
    ) {
        return ProfilePatchRequest.builder()
                .nickname(nickname)
                .description(description)
                .categoryIds(categoryIds)
                .rawPassword(rawPassword)
                .profileImg(profileImg)
                .descImagesAdd(descImagesAdd)
                .descImagesDeleteIds(descImagesDeleteIds)
                .build();
    }
}
