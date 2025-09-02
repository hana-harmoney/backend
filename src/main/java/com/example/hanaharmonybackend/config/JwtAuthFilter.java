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
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwt;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        // 개발용 OPTIONS 허용 (배포시 삭제 권장)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                // typ 클레임으로 구분 (access / refresh / delegate)
                String typ = jwt.parseTokenType(token);

                // 🔹 refresh 토큰은 무시
                if ("refresh".equalsIgnoreCase(typ)) {
                    chain.doFilter(request, response);
                    return;
                }

                // 🔹 delegate 토큰 처리
                if ("delegate".equalsIgnoreCase(typ)) {
                    Map<String, Object> claims = jwt.parseAllClaims(token);
                    String scope = (String) claims.get("scope");
                    if ("PROFILE_CREATE".equals(scope)) {
                        request.setAttribute("delegate", true);
                        request.setAttribute("scope", scope);
                        request.setAttribute("userIdScope", Long.valueOf(claims.get("userIdScope").toString()));

                        // delegate 토큰도 Authentication 만들어 SecurityContext에 넣어주기
                        var auth = new UsernamePasswordAuthenticationToken(
                                "delegateUser", // principal (임의값)
                                null,
                                Collections.emptyList()
                        );
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                    chain.doFilter(request, response);
                    return;
                }

                // 🔹 일반 access 토큰 처리
                Long uid = jwt.parseUserId(token);

                User user = userRepository.findById(uid)
                        .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

                var auth = new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        Collections.emptyList()
                );
                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (Exception ignored) {
                // 토큰 문제 있으면 익명으로 진행 (엔드포인트에서 401 처리)
            }
        }

        chain.doFilter(request, response);
    }
}