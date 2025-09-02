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

    // 토큰 관련
    INVALID_TOKEN("T001", "유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED("T002", "만료된 토큰입니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_ALREADY_USED("T003", "이미 사용된 토큰입니다.", HttpStatus.BAD_REQUEST),
    INVALID_SCOPE("T004", "잘못된 권한 범위입니다.", HttpStatus.FORBIDDEN),

    // User 관련 error
    BAD_CREDENTIALS("E004", "아이디 또는 비밀번호가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED),
    DUPLICATE_LOGIN_ID("E005", "이미 사용 중인 아이디입니다.", HttpStatus.CONFLICT),
    USER_NOT_FOUND("E006", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    USER_DELETED("E008", "탈퇴한 계정입니다.",HttpStatus.CONFLICT),
    BAD_PASS("E009","비밀번호가 일치하지 않습니다", HttpStatus.BAD_REQUEST),
    SAME_PASS("E010","동일한 비밀번호 입니다", HttpStatus.UNAUTHORIZED),
  
    // Account 관련 error
    ACCOUNT_NOT_FOUND("E007", "사용자의 계좌를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INSUFFICIENT_ACCOUNT_BALANCE("E008", "계좌 잔액이 부족합니다.", HttpStatus.BAD_REQUEST),

    //Board 관련 error
    BOARD_NOT_FOUND("B001", "존재하지 않는 게시글입니다.", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_FOUND("C001", "카테고리를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PROFILE_NOT_FOUND("U002", "프로필이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    BOARD_NOT_WRITER("B002", "게시글 작성자가 아닙니다.", HttpStatus.UNAUTHORIZED),
    USER_LOCATION_REQUIRED("E011","사용자의 위치 정보가 필요합니다.", HttpStatus.BAD_REQUEST),

    //Pocket error
    POCKET_NOT_FOUND("P001", "주머니가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    ACCOUNT_ACCESS_DENIED("P002", "계좌 소유자가 아닙니다.", HttpStatus.UNAUTHORIZED),
    POCKET_ACCESS_DENIED("P003", "주머니 소유자가 아닙니다.", HttpStatus.UNAUTHORIZED),
    INSUFFICIENT_POCKET_BALANCE("P004", "주머니 잔액이 부족합니다.", HttpStatus.BAD_REQUEST),

    //Chat 관련 error
    CHATROOM_NOT_FOUND("C001", "존재하지 않는 채팅방입니다.", HttpStatus.NOT_FOUND),
    CHATROOM_ACCESS_DENIED("C002", "채팅방에 접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
    INVALID_REVIEW_SCORE("C003", "유효하지 않은 리뷰 점수입니다. 허용값: -0.5, 0.5, 1.0", HttpStatus.BAD_REQUEST),
    CHATROOM_TRANSFER_DENIED("C004", "게시글 작성자만 송금할 수 있습니다", HttpStatus.FORBIDDEN),
    INVALID_TRANSFER_AMOUNT("C005", "송금 금액은 0원 이상이어야 합니다.", HttpStatus.BAD_REQUEST);

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