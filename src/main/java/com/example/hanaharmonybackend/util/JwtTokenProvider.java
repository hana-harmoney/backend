package com.example.hanaharmonybackend.util;

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

    @Value("${jwt.ttlMillis:3600000}")
    private long ttlMillis;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(Long userId, String loginId) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .claim("uid", userId)
                .claim("login", loginId)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + ttlMillis))
                .signWith(key())
                .compact();
    }

    public Long parseUserId(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody()
                .get("uid", Number.class).longValue();
    }
}
