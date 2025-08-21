package com.example.hanaharmonybackend.config;

import com.example.hanaharmonybackend.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // JWT용 기본 설정
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(m -> m.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // ✅ 기본 로그인 폼/HTTP Basic 비활성화 (로그인 폼 사라짐)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // ✅ 공개 경로 허용
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",                         // 루트
                                "/auth/signup", "/auth/login",
                                "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                // ✅ 미인증/권한없음 시 JSON으로 응답
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> {
                            res.setStatus(HttpStatus.UNAUTHORIZED.value());
                            res.setContentType("application/json;charset=UTF-8");
                            res.getWriter().write("{\"code\":\"E401\",\"message\":\"인증이 필요합니다.\",\"result\":null}");
                        })
                        .accessDeniedHandler((req, res, e) -> {
                            res.setStatus(HttpStatus.FORBIDDEN.value());
                            res.setContentType("application/json;charset=UTF-8");
                            res.getWriter().write("{\"code\":\"E403\",\"message\":\"접근 권한이 없습니다.\",\"result\":null}");
                        })
                );

        // ✅ JWT 필터 삽입
        http.addFilterBefore(new JwtAuthFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
