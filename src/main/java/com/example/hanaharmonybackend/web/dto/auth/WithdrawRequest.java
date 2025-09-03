package com.example.hanaharmonybackend.web.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record WithdrawRequest(
        @NotBlank String current_password
) {}
