package com.example.hanaharmonybackend.web.dto.profile;

import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Builder
public record ProfilePatchRequest(
        String nickname,
        String description,
        List<Long> categoryIds,
        String currentPassword,              // 추가: 현재 비밀번호
        String newPassword,                  // 추가: 새 비밀번호
        MultipartFile profileImg,
        List<MultipartFile> descImagesAdd,
        List<Long> descImagesDeleteIds
) {
    public static ProfilePatchRequest of(
            String nickname, String description, List<Long> categoryIds,
            String currentPassword, String newPassword,               // 시그니처 변경
            MultipartFile profileImg, List<MultipartFile> descImagesAdd,
            List<Long> descImagesDeleteIds
    ) {
        return ProfilePatchRequest.builder()
                .nickname(nickname)
                .description(description)
                .categoryIds(categoryIds)
                .currentPassword(currentPassword)   
                .newPassword(newPassword)
                .profileImg(profileImg)
                .descImagesAdd(descImagesAdd)
                .descImagesDeleteIds(descImagesDeleteIds)
                .build();
    }
}
