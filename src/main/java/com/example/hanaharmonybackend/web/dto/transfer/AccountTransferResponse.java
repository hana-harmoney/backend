package com.example.hanaharmonybackend.web.dto.transfer;

import lombok.AllArgsConstructor;
import lombok.Getter;

// 계좌 → 계좌 응답
@Getter
@AllArgsConstructor
public class AccountTransferResponse {
    private Long change;          // 내 계좌 잔액
    private String toAccountNum;  // 상대 계좌번호
    private String toAccountName; // 상대 계좌주
    private Long amount;          // 송금 금액
}
