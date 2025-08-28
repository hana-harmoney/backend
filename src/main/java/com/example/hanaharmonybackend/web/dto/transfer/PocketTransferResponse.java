package com.example.hanaharmonybackend.web.dto.transfer;

import lombok.AllArgsConstructor;
import lombok.Getter;

// 계좌 <-> 주머니 응답
@Getter
@AllArgsConstructor
public class PocketTransferResponse {
    private Long change;       // 계좌 잔액
    private Long pocketAmount; // 주머니 잔액
    private Long amount;       // 이체 금액
}
