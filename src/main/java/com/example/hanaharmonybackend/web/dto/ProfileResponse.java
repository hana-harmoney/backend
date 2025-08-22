package com.example.hanaharmonybackend.web.dto;

import java.util.List;

public record ProfileResponse(
        String nickname,
        String profile_img,
        List<String> category_ids,
        String description,
        List<String> img_url,
        int trust,
        int match_count,
        int report_count
) {}
