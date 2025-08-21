package com.example.hanaharmonybackend.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        @JsonProperty("user_id") Long userId,
        String name
) {}
