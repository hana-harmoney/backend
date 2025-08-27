package com.example.hanaharmonybackend.service.serviceImpl;

import com.example.hanaharmonybackend.domain.Account;
import com.example.hanaharmonybackend.domain.User;
import com.example.hanaharmonybackend.payload.code.ErrorStatus;
import com.example.hanaharmonybackend.payload.exception.CustomException;
import com.example.hanaharmonybackend.repository.AccountRepository;
import com.example.hanaharmonybackend.repository.ProfileRepository;
import com.example.hanaharmonybackend.repository.UserRepository;
import com.example.hanaharmonybackend.service.AccountCommandService;
import com.example.hanaharmonybackend.service.AuthService;
import com.example.hanaharmonybackend.util.JwtTokenProvider;
import com.example.hanaharmonybackend.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.hanaharmonybackend.payload.code.ErrorStatus.INVALID_INPUT;
import static com.example.hanaharmonybackend.payload.code.ErrorStatus.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AccountCommandService accountCommandService;
    private final AccountRepository accountRepository;
    private final ProfileRepository profileRepository;

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest req) {
        User user = userRepository.findByLoginId(req.loginId())
                .orElseThrow(() -> new CustomException(ErrorStatus.BAD_CREDENTIALS));

        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new CustomException(ErrorStatus.BAD_CREDENTIALS);
        }
        if (user.getIsDeleted()) {  // or Boolean.TRUE.equals(user.getIsDeleted())
            throw new CustomException(ErrorStatus.BAD_CREDENTIALS);
            // 특정 메시지를 원하면 전용 에러코드 추가해서 던지세요 (ex. ACCOUNT_DELETED)
        }

        String access  = jwtTokenProvider.createAccessToken(user.getId(), user.getLoginId());
        String refresh = jwtTokenProvider.createRefreshToken(user.getId(), user.getLoginId());

        boolean hasProfile = profileRepository.existsByUser_Id(user.getId()); //

        return new LoginResponse(access, refresh, user.getId(), user.getName(), hasProfile);
    }

    @Override
    @Transactional // readOnly 금지
    public void withdraw(Long userId, String reason) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        if (user.getIsDeleted()) { // 우리가 추가한 게터
            throw new CustomException(INVALID_INPUT);
        }

        user.setIsDeleted(true);    // 우리가 추가한 세터
        userRepository.save(user);  // 명시 저장(안전)
        userRepository.flush();     // 즉시 반영(선택)

        accountRepository.softDeleteByUserId(userId);

        // 현재 세션/컨텍스트에 캐시된 이전 사용자 객체 제거
        SecurityContextHolder.clearContext();
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
