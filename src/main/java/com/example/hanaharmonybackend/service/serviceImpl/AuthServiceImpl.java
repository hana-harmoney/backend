package com.example.hanaharmonybackend.service.serviceImpl;

import com.example.hanaharmonybackend.domain.Account;
import com.example.hanaharmonybackend.domain.User;
import com.example.hanaharmonybackend.payload.code.ErrorStatus;
import com.example.hanaharmonybackend.payload.exception.CustomException;
import com.example.hanaharmonybackend.repository.UserRepository;
import com.example.hanaharmonybackend.service.AccountCommandService;
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
    private final AccountCommandService accountCommandService;

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
        if (userRepository.existsByLoginId(req.loginId())) {
            throw new CustomException(ErrorStatus.DUPLICATE_LOGIN_ID);
        }

        User user = User.builder()
                .loginId(req.loginId())
                .password(passwordEncoder.encode(req.password()))
                .name(req.name())
                .birth(req.birth())
                .gender(req.gender())
                .phone(req.phone())
                .address(req.address())
                .deleted(false)
                .build();

        User saved = userRepository.save(user);

        // 2) 계좌 생성
        Account account = accountCommandService.createFor(user);
        user.setAccount(account);

        return new SignupResponse(saved.getId(), saved.getLoginId(), saved.getName());
    }
}
