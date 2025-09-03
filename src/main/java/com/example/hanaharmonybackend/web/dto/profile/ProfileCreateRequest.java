package com.example.hanaharmonybackend.web.dto.profile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record ProfileCreateRequest(
        @NotBlank @Size(max = 50) String nickname,
        @Size(max = 3000) String description,
        String profile_img,
        List<Long> category_ids,
        List<String> img_url
) {}
