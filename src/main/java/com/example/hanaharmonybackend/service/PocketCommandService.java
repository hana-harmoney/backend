package com.example.hanaharmonybackend.service;

import com.example.hanaharmonybackend.web.dto.pocket.PocketCreateRequest;
import com.example.hanaharmonybackend.web.dto.pocket.PocketCreateResponse;

public interface PocketCommandService {
  PocketCreateResponse create(Long accountId, PocketCreateRequest req, Long requesterId);
  void delete(Long pocketId, Long requesterId);
}
