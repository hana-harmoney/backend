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

import static com.example.hanaharmonybackend.payload.code.ErrorStatus.*;

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
    public void withdraw(Long userId, String currentPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        // 이미 탈퇴
        if (user.getIsDeleted()) {
            throw new CustomException(USER_DELETED); // 메시지 노출 막기용 (원하면 별도 코드 추가)
        }

        // 현재 비밀번호 검증
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new CustomException(BAD_PASS);
        }

        // 소프트 삭제
        user.setIsDeleted(true);
        // 필요 시: user.setDeleteReason(reason); user.setDeletedAt(LocalDateTime.now());

        userRepository.save(user);   // 명시 저장
        userRepository.flush();      // 즉시 반영 (선택)

        // 현재 세션 종료
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
