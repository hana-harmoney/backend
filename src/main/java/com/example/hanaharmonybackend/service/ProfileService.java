package com.example.hanaharmonybackend.service;

import com.example.hanaharmonybackend.web.dto.ProfileCreateRequest;
import com.example.hanaharmonybackend.web.dto.ProfilePatchRequest;
import com.example.hanaharmonybackend.web.dto.ProfileResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProfileService {
    ProfileResponse create(Long currentUserId, ProfileCreateRequest req);

    // 파일 수신 + S3 업로드 후 저장
    ProfileResponse createWithFiles(
            Long currentUserId,
            String nickname,
            String description,
            List<Long> categoryIds,
            MultipartFile profileImg,
            List<MultipartFile> descImages
    );
    ProfileResponse patch(Long userId, ProfilePatchRequest req);
    ProfileResponse getMyProfile(Long currentUserId);
}