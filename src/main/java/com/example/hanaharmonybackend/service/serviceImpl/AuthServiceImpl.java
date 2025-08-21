package com.example.hanaharmonybackend.service.serviceImpl;

import com.example.hanaharmonybackend.domain.User;
import com.example.hanaharmonybackend.payload.code.ErrorStatus;
import com.example.hanaharmonybackend.payload.excpetion.CustomException;
import com.example.hanaharmonybackend.repository.UserRepository;
import com.example.hanaharmonybackend.service.AuthService;
import com.example.hanaharmonybackend.web.dto.SignupRequest;
import com.example.hanaharmonybackend.web.dto.SignupResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public SignupResponse signup(SignupRequest req) {
        if (userRepository.existsByLoginId(req.loginId())) {
            // ErrorStatus에 DUPLICATE_LOGIN_ID 없으면 추가하세요.
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
        return new SignupResponse(saved.getId(), saved.getLoginId(), saved.getName());
    }
}
