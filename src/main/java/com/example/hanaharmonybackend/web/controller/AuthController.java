package com.example.hanaharmonybackend.web.controller;

import com.example.hanaharmonybackend.payload.ApiResponse;
import com.example.hanaharmonybackend.service.AuthService;
import com.example.hanaharmonybackend.web.dto.SignupRequest;
import com.example.hanaharmonybackend.web.dto.SignupResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/signup", consumes = "application/json", produces = "application/json")
    public ApiResponse<SignupResponse> signup(@RequestBody @Valid SignupRequest req) {
        return ApiResponse.success(authService.signup(req));
    }
}
