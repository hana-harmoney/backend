package com.example.hanaharmonybackend.repository;

import com.example.hanaharmonybackend.domain.ProxyAccess;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProxyAccessRepository extends JpaRepository<ProxyAccess, Long> {

    // delegateToken 으로 ProxyAccess 조회
    Optional<ProxyAccess> findByDelegateToken(String delegateToken);

    // ownerUserId로 위임 토큰 조회 (필요시)
    Optional<ProxyAccess> findByOwnerUserId(Long ownerUserId);
}
