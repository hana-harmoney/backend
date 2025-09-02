package com.example.hanaharmonybackend.service.serviceImpl;

import com.example.hanaharmonybackend.domain.ProxyAccess;
import com.example.hanaharmonybackend.repository.ProfileRepository;
import com.example.hanaharmonybackend.repository.ProxyAccessRepository;
import com.example.hanaharmonybackend.service.ProxyAccessService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class ProxyAccessServiceImpl implements ProxyAccessService {
    private final ProxyAccessRepository repository;

    @Override
    public ProxyAccess createForProfileCreate(Long ownerUserId) {
        String token = RandomStringUtils.randomAlphanumeric(40);
        ProxyAccess pa = ProxyAccess.builder()
                .ownerUserId(ownerUserId)
                .profileId(null) // 생성 전이므로 없음
                .delegateToken(token)
                .expiresAt(Instant.now().plus(24, ChronoUnit.HOURS))
                .scope("PROFILE_CREATE")
                .used(false)
                .build();
        return repository.save(pa);
    }

    @Override
    public ProxyAccess validateUsable(String token) {
        ProxyAccess pa = repository.findByDelegateToken(token)
                .orElseThrow(() -> new IllegalArgumentException("INVALID_TOKEN"));
        if (!"PROFILE_CREATE".equals(pa.getScope())) throw new IllegalArgumentException("INVALID_SCOPE");
        if (pa.isUsed()) throw new IllegalArgumentException("TOKEN_ALREADY_USED");
        if (pa.getExpiresAt().isBefore(Instant.now())) throw new IllegalArgumentException("TOKEN_EXPIRED");
        return pa;
    }

    @Override
    public void markUsed(ProxyAccess pa) {
        pa.setUsed(true);
        repository.save(pa);
    }
}

