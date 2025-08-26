package com.example.hanaharmonybackend.web.controller;

import com.example.hanaharmonybackend.payload.ApiResponse;
import com.example.hanaharmonybackend.payload.code.SuccessStatus;
import com.example.hanaharmonybackend.service.AuthService;
import com.example.hanaharmonybackend.util.SecurityUtil;
import com.example.hanaharmonybackend.web.dto.LoginRequest;
import com.example.hanaharmonybackend.web.dto.LoginResponse;
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
    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public ApiResponse<LoginResponse> login(@RequestBody @Valid LoginRequest req) {
        LoginResponse data = authService.login(req);
        return new ApiResponse<>(SuccessStatus.OK.getCode(), "로그인에 성공했습니다.", data);
    }
    @PostMapping(value = "/withdraw", produces = "application/json")
    public ApiResponse<String> withdraw() {
        var me = SecurityUtil.getCurrentMember();
        authService.withdraw(me.getId(), null);
        return new ApiResponse<>(SuccessStatus.OK.getCode(), "회원탈퇴에 성공했습니다.", "OK");
    }

}
