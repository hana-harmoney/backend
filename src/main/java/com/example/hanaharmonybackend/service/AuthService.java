package com.example.hanaharmonybackend.service;

import com.example.hanaharmonybackend.web.dto.auth.LoginRequest;
import com.example.hanaharmonybackend.web.dto.auth.LoginResponse;
import com.example.hanaharmonybackend.web.dto.auth.SignupRequest;
import com.example.hanaharmonybackend.web.dto.auth.SignupResponse;

public interface AuthService {
    SignupResponse signup(SignupRequest req);
    LoginResponse login(LoginRequest req);
    void withdraw(Long userId, String currentPassword);
    boolean checkLoginId(String loginId);
    String findFirebaseTokenByUserId(Long userId);
}
