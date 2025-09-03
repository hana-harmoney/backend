package com.example.hanaharmonybackend.web.controller;

import com.example.hanaharmonybackend.domain.ProxyAccess;
import com.example.hanaharmonybackend.domain.User;
import com.example.hanaharmonybackend.payload.ApiResponse;
import com.example.hanaharmonybackend.service.ProfileService;
import com.example.hanaharmonybackend.service.ProxyAccessService;
import com.example.hanaharmonybackend.util.JwtTokenProvider;
import com.example.hanaharmonybackend.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Delegate", description = "자녀 위임 프로필 등록 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/delegate")
public class DelegateController {
    private final ProxyAccessService proxyAccessService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ProfileService profileService;

    // A) 생성 전용 링크 발급 (로그인 필요)
    @Operation(summary = "생성 전용 링크 발급 (로그인 필요)")
    @PostMapping("/links/create")
    public ApiResponse<?> createLinkForCreate() {
        User me = SecurityUtil.getCurrentMember();
        Long ownerUserId = me.getId();

        if (profileService.existsByUserId(ownerUserId)) {
            return ApiResponse.error("E_ALREADY_HAS_PROFILE","이미 프로필이 존재합니다.", null);
        }

        ProxyAccess pa = proxyAccessService.createForProfileCreate(ownerUserId);
        String link = "https://hanaharmoney.com/delegate?token=" + pa.getDelegateToken();

        return ApiResponse.success(Map.of(
                "delegateToken", pa.getDelegateToken(),
                "shareLink", link,
                "expiresAt", pa.getExpiresAt().toString()
        ));
    }

    // B) 자녀가 제한 JWT로 교환 (비로그인)
    @Operation(summary = "자녀가 제한 JWT로 교환 (비로그인)")
    @PostMapping("/session/create")
    public ApiResponse<?> exchangeForCreate(@RequestParam String token) {
        ProxyAccess pa = proxyAccessService.validateUsable(token);

        String delegateJwt = jwtTokenProvider.issueDelegateJwt(
                "delegate:create:" + pa.getOwnerUserId(),
                Map.of(
                        "scope", "PROFILE_CREATE",
                        "userIdScope", pa.getOwnerUserId()
                ),
                60 * 60 * 2  // 2시간 유효 - JWT의 만료시간
        );

        // 발급 즉시 단일 사용 처리
        proxyAccessService.markUsed(pa);

        return ApiResponse.success(Map.of("token", delegateJwt));
    }
}

