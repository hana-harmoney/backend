package com.example.hanaharmonybackend.payload.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseStatus {
    INVALID_INPUT("E001", "Invalid input provided", HttpStatus.BAD_REQUEST),
    SERVER_ERROR("E002", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHORIZED("E003", "권한이 없습니다.", HttpStatus.UNAUTHORIZED),

    // User 관련 error
    BAD_CREDENTIALS("E004", "아이디 또는 비밀번호가 올바르지 않습니다.", org.springframework.http.HttpStatus.UNAUTHORIZED),
    DUPLICATE_LOGIN_ID("E005", "이미 사용 중인 아이디입니다.", HttpStatus.CONFLICT),
    USER_NOT_FOUND("E006", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    @Override
    public ReasonDto getReason() {
        return ReasonDto.builder()
                .httpStatus(httpStatus)
                .message(message)
                .code(code)
                .build();
    }

}