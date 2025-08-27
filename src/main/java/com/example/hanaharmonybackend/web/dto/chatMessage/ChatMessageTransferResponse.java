package com.example.hanaharmonybackend.web.dto.chatMessage;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatMessageTransferResponse {
    private Long change;          // 내 계좌 잔액
    private String toAccountNum;  // 상대 계좌번호
    private String toAccountName; // 상대 계좌주
    private String toAccountNickname;   // 상대 계좌주 닉네임
    private Long amount;          // 송금 금액
    private ChatMessageResponse chatMessage;    // 채팅 메세지
}