package com.example.hanaharmonybackend.service;

import com.example.hanaharmonybackend.web.dto.pocket.PocketDetailResponse;

public interface PocketQueryService {
  PocketDetailResponse getDetail(Long pocketId, Long requesterId);
}
