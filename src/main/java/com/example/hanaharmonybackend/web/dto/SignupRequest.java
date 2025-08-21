package com.example.hanaharmonybackend.web.dto;

import com.example.hanaharmonybackend.domain.enumerate.GENDER;
import jakarta.validation.constraints.*;

public record SignupRequest(
        @NotBlank @Size(min = 4, max = 20, message = "아이디는 4~20자여야 합니다.")
        String loginId,

        // 프로젝트 규칙: 특수문자 허용, 길이만 검증
        @NotBlank @Size(min = 8, max = 20, message = "비밀번호는 8~20자여야 합니다.")
        String password,

        @NotBlank @Size(max = 20, message = "이름은 20자 이내여야 합니다.")
        String name,

        @NotBlank @Size(max = 10, message = "생년월일 형식이 올바르지 않습니다.")
        String birth,

        @NotNull(message = "성별은 필수입니다.")
        GENDER gender,

        @NotBlank @Size(max = 30, message = "전화번호는 30자 이내여야 합니다.")
        String phone,

        @NotBlank @Size(max = 100, message = "주소는 100자 이내여야 합니다.")
        String address
) {}
