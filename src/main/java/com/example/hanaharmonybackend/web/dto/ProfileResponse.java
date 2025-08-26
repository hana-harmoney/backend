package com.example.hanaharmonybackend.web.dto;

import java.util.List;

public record ProfileResponse(
        String nickname,
        String profile_img,
        List<Long> category_ids,
        String description,
        List<ImageItem> img_url_detail,
        int trust,
        int match_count,
        int report_count
) {
    public record ImageItem(Long id, String url) {}
}
