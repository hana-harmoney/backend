package com.example.hanaharmonybackend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class ProxyAccess {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long ownerUserId;   // 위임하는 유저(시니어)

    // 생성 전이므로 null 허용
    private Long profileId;

    @Column(unique = true, nullable = false, length = 128)
    private String delegateToken;

    private Instant expiresAt;  // 예: 24h
    private boolean used;       // 단일 사용 정책 시 true로

    // 단일 스코프만 사용
    private String scope; // 항상 "PROFILE_CREATE"
}
