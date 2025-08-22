package com.example.hanaharmonybackend.web.controller;

import com.example.hanaharmonybackend.domain.User;
import com.example.hanaharmonybackend.payload.ApiResponse;
import com.example.hanaharmonybackend.service.ProfileService;
import com.example.hanaharmonybackend.util.SecurityUtil;
import com.example.hanaharmonybackend.web.dto.ProfileCreateRequest;
import com.example.hanaharmonybackend.web.dto.ProfileResponse;
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
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ApiResponse<ProfileResponse> create(@RequestBody @Valid ProfileCreateRequest req) {
        User me = SecurityUtil.getCurrentMember();
        return ApiResponse.success(profileService.create(me.getId(), req));
    }

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
    @GetMapping(produces = "application/json")
    public ApiResponse<ProfileResponse> getMyProfile() {
        User me = SecurityUtil.getCurrentMember();
        return ApiResponse.success(profileService.getMyProfile(me.getId()));
    }
}
