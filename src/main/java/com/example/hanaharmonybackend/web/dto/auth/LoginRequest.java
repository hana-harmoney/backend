package com.example.hanaharmonybackend.web.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank @Size(min = 4, max = 20, message = "아이디는 4~20자여야 합니다.")
        String loginId,
        @NotBlank @Size(min = 8, max = 20, message = "비밀번호는 8~20자여야 합니다.")
        String password
) {}
