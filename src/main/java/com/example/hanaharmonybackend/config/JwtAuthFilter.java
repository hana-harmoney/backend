package com.example.hanaharmonybackend.config;

import com.example.hanaharmonybackend.domain.User;
import com.example.hanaharmonybackend.payload.code.ErrorStatus;
import com.example.hanaharmonybackend.payload.exception.CustomException;
import com.example.hanaharmonybackend.repository.UserRepository;
import com.example.hanaharmonybackend.util.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwt;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                String typ = jwt.parseTokenType(token);
                if ("refresh".equalsIgnoreCase(typ)) {
                    chain.doFilter(request, response);
                    return;
                }

                Long uid = jwt.parseUserId(token);

                //principal = User 엔티티로 세팅
                User user = userRepository.findById(uid)
                        .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

                var auth = new UsernamePasswordAuthenticationToken(
                        user,                     // principal = User
                        null,
                        Collections.emptyList()   // 권한 필요 없으면 빈 리스트
                );
                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (Exception ignored) {
                // 토큰 문제면 익명으로 계속 진행 (엔드포인트에서 401 처리)
            }
        }

        chain.doFilter(request, response);
    }
}
