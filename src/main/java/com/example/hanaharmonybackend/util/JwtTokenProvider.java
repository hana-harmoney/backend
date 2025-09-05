package com.example.hanaharmonybackend.util;

import com.example.hanaharmonybackend.domain.User;
import com.example.hanaharmonybackend.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret:change-me-32bytes-minimum-secret-key!}")
    private String secret;

    @Value("${jwt.accessTtlMillis:${jwt.ttlMillis:3600000}}")
    private long accessTtlMillis;

    @Value("${jwt.refreshTtlMillis:1209600000}") // 14일
    private long refreshTtlMillis;

    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
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
                .signWith(key)
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
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.warn("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty.", e);
        }
        return false;
    }

    // Delegate 토큰 발급
    public String issueDelegateJwt(String subject, Map<String, Object> extraClaims, long expireSeconds) {
        long now = System.currentTimeMillis();
        Date expiry = new Date(now + expireSeconds * 1000);

        JwtBuilder builder = Jwts.builder()
                .setSubject(subject)
                .claim("typ", "delegate") // delegate 타입 명시
                .setIssuedAt(new Date(now))
                .setExpiration(expiry)
                .signWith(key);

        if (extraClaims != null) {
            extraClaims.forEach(builder::claim);
        }

        return builder.compact();
    }

    // Claims 전체 조회 (delegate scope 확인용)
    public Map<String, Object> parseAllClaims(String token) {
        Claims claims = parseClaims(token);
        return new HashMap<>(claims);
    }

    private final UserRepository userRepository;

    public Authentication getAuthentication(String token) {
        // 1) 토큰 유효성
        if (!validateToken(token)) return null;

        // (선택) access 토큰만 허용
        String typ = parseTokenType(token);
        if (typ != null && !"access".equals(typ)) return null;

        // 2) 토큰에서 "login"(또는 subject)을 꺼내서 유저 로드
        String loginId = parseLoginId(token); // createAccessToken에서 claim("login", loginId) 넣었음
        if (loginId == null) return null;

        // 4) DB에서 사용자 조회
        User user = userRepository.findByLoginId(loginId)
            .orElseThrow(() -> new UsernameNotFoundException("user '" + loginId + "' not found"));

        // 6) Authentication 생성 (credentials에는 토큰 or 빈 문자열)
        return new UsernamePasswordAuthenticationToken(user, token, null);
    }
}