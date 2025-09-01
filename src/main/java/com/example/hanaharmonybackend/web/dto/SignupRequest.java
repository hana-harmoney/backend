package com.example.hanaharmonybackend.web.dto;

import com.example.hanaharmonybackend.domain.enumerate.GENDER;
import jakarta.validation.constraints.*;

public record SignupRequest(
        // 아이디: 영문, 숫자로만 구성, 4~20자
        @NotBlank(message = "아이디는 필수입니다.")
        @Size(min = 4, max = 20, message = "아이디는 4~20자여야 합니다.")
        @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "아이디는 영문, 숫자만 사용 가능합니다.")
        String loginId,

        // 비밀번호: 영문, 숫자, 특수문자 조합, 8~20자
        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 8, max = 20, message = "비밀번호는 8~20자여야 합니다.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]*$", message = "비밀번호는 영문, 숫자, 특수문자를 모두 포함해야 합니다.")
        String password,

        // 이름: 한글만 사용, 2~20자
        @NotBlank(message = "이름은 필수입니다.")
        @Size(min = 2, max = 20, message = "이름은 2~20자여야 합니다.")
        @Pattern(regexp = "^[가-힣]*$", message = "이름은 한글만 사용 가능합니다.")
        String name,

        // 생년월일: YYYYMMDD 형식, 1900년 이후
        @NotBlank(message = "생년월일은 필수입니다.")
        @Pattern(regexp = "^(19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])$", message = "생년월일 형식이 올바르지 않습니다. (YYYYMMDD)")
        String birth,

        // 성별
        @NotNull(message = "성별은 필수입니다.")
        GENDER gender,

        // 전화번호: 010-XXXX-XXXX 형식
        @NotBlank(message = "전화번호는 필수입니다.")
        @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다. (010-1234-5678)")
        String phone,

        // 주소
        @NotBlank(message = "주소는 필수입니다.")
        @Size(max = 100, message = "주소는 100자 이내여야 합니다.")
        String address,

        // 위도 (카카오맵 y)
        @NotNull(message = "위도는 필수입니다.")
        Double latitude,

        // 경도 (카카오맵 x)
        @NotNull(message = "경도는 필수입니다.")
        Double longitude
) {}
