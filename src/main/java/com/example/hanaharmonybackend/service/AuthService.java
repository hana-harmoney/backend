package com.example.hanaharmonybackend.service;

import com.example.hanaharmonybackend.web.dto.LoginRequest;
import com.example.hanaharmonybackend.web.dto.LoginResponse;
import com.example.hanaharmonybackend.web.dto.SignupRequest;
import com.example.hanaharmonybackend.web.dto.SignupResponse;

public interface AuthService {
    SignupResponse signup(SignupRequest req);
    LoginResponse login(LoginRequest req);
}
