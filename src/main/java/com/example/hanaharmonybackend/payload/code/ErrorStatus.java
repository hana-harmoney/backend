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
    USER_NOT_FOUND("E006", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // Account 관련 error
    ACCOUNT_NOT_FOUND("E007", "사용자의 계좌를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    //Board 관련 error
    BOARD_NOT_FOUND("B001", "존재하지 않는 게시글입니다.", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_FOUND("C001", "카테고리를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PROFILE_NOT_FOUND("U002", "프로필이 존재하지 않습니다.", HttpStatus.NOT_FOUND),

    //Pocket error
    POCKET_NOT_FOUND("P001", "주머니가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    ACCOUNT_ACCESS_DENIED("P002", "계좌 소유자가 아닙니다.",HttpStatus.UNAUTHORIZED);

    //Chat 관련 error
    CHATROOM_NOT_FOUND("C001", "존재하지 않는 채팅방입니다.", HttpStatus.NOT_FOUND),
    CHATROOM_ACCESS_DENIED("C002", "채팅방에 접근 권한이 없습니다.", HttpStatus.FORBIDDEN);

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