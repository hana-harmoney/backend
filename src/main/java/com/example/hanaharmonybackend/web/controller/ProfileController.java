package com.example.hanaharmonybackend.web.controller;

import com.example.hanaharmonybackend.domain.User;
import com.example.hanaharmonybackend.payload.ApiResponse;
import com.example.hanaharmonybackend.service.ProfileService;
import com.example.hanaharmonybackend.util.SecurityUtil;
import com.example.hanaharmonybackend.web.dto.ProfileCreateRequest;
import com.example.hanaharmonybackend.web.dto.ProfilePatchRequest;
import com.example.hanaharmonybackend.web.dto.ProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;

    /** (기존) JSON으로 URL 받아 저장 */
    @Operation(summary = "프로필 생성")
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ApiResponse<ProfileResponse> create(@RequestBody @Valid ProfileCreateRequest req) {
        User me = SecurityUtil.getCurrentMember();
        return ApiResponse.success(profileService.create(me.getId(), req));
    }

    /** (기존) 멀티파트 업로드로 프로필 생성 */
    @Operation(summary = "프로필 생성")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "application/json")
    public ApiResponse<ProfileResponse> createWithFiles(
            @RequestParam("nickname") @NotBlank String nickname,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "category_ids", required = false) List<Long> categoryIds,
            @RequestPart(value = "profile_img", required = false) MultipartFile profileImg,
            @RequestPart(value = "desc_images", required = false) List<MultipartFile> descImages
    ) {
        User me = SecurityUtil.getCurrentMember();
        return ApiResponse.success(
                profileService.createWithFiles(me.getId(), nickname, description, categoryIds, profileImg, descImages)
        );
    }

    /** 내 프로필 조회 (GET /profile) */
    @Operation(summary = "내 프로필 조회")
    @GetMapping(produces = "application/json")
    public ApiResponse<ProfileResponse> getMyProfile() {
        User me = SecurityUtil.getCurrentMember();
        return ApiResponse.success(profileService.getMyProfile(me.getId()));
    }

    /** 프로필 부분 수정 (PATCH /profile) */
    @Operation(summary = "프로필 수정")
    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "application/json")
    public ApiResponse<ProfileResponse> patchProfile(
            @RequestParam(value = "nickname", required = false) String nickname,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "category_ids", required = false) List<Long> categoryIds,
            @RequestParam(value = "current_password", required = false) String currentPassword,
            @RequestParam(value = "new_password",     required = false) String newPassword,
            @RequestPart(value = "profile_img", required = false) MultipartFile profileImg,
            // 소개 사진 조작
            @RequestPart(value = "desc_images", required = false) List<MultipartFile> descImagesAdd,
            @RequestParam(value = "desc_images_delete_ids", required = false) List<Long> descImagesDeleteIds
    ) {
        User me = SecurityUtil.getCurrentMember();
        var req = ProfilePatchRequest.of(
                nickname, description, categoryIds, currentPassword,newPassword,
                profileImg, descImagesAdd, descImagesDeleteIds
        );
        return ApiResponse.success(profileService.patch(me.getId(), req));
    }
}
