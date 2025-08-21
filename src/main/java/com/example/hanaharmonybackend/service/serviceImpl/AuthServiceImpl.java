package com.example.hanaharmonybackend.service.serviceImpl;

import com.example.hanaharmonybackend.domain.User;
import com.example.hanaharmonybackend.payload.code.ErrorStatus;
import com.example.hanaharmonybackend.payload.excpetion.CustomException; // 오탈자 패키지 그대로
import com.example.hanaharmonybackend.repository.UserRepository;
import com.example.hanaharmonybackend.service.AuthService;
import com.example.hanaharmonybackend.util.JwtTokenProvider;
import com.example.hanaharmonybackend.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest req) {
        User user = userRepository.findByLoginId(req.loginId())
                .orElseThrow(() -> new CustomException(ErrorStatus.BAD_CREDENTIALS));

        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new CustomException(ErrorStatus.BAD_CREDENTIALS);
        }

        String access  = jwtTokenProvider.createAccessToken(user.getId(), user.getLoginId());
        String refresh = jwtTokenProvider.createRefreshToken(user.getId(), user.getLoginId());

        return new LoginResponse(access, refresh, user.getId(), user.getName());
    }

    @Override
    @Transactional
    public SignupResponse signup(SignupRequest req) {
        // 네가 이미 구현한 회원가입 로직 사용 (여기선 생략)
        throw new UnsupportedOperationException("signup is implemented elsewhere");
    }
}
