package com.example.hanaharmonybackend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret:change-me-32bytes-minimum-secret-key!}")
    private String secret;

    @Value("${jwt.accessTtlMillis:${jwt.ttlMillis:3600000}}")
    private long accessTtlMillis;

    @Value("${jwt.refreshTtlMillis:1209600000}") // 14일
    private long refreshTtlMillis;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(Long userId, String loginId) {
        return buildToken(userId, loginId, accessTtlMillis, "access");
    }

    public String createRefreshToken(Long userId, String loginId) {
        return buildToken(userId, loginId, refreshTtlMillis, "refresh");
    }

    private String buildToken(Long userId, String loginId, long ttl, String type) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .claim("uid", userId)
                .claim("login", loginId)
                .claim("typ", type)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + ttl))
                .signWith(key())
                .compact();
    }

    // JwtAuthFilter가 호출하는 메서드
    public Long parseUserId(String token) {
        Number n = parseClaims(token).get("uid", Number.class);
        return n == null ? null : n.longValue();
    }

    // (선택) 필요하면 로그인 아이디/토큰 타입도 파싱 가능
    public String parseLoginId(String token) {
        return parseClaims(token).get("login", String.class);
    }

    public String parseTokenType(String token) {
        return parseClaims(token).get("typ", String.class); // "access" | "refresh"
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
