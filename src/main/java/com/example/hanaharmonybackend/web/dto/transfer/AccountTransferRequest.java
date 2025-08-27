package com.example.hanaharmonybackend.web.dto.transfer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 계좌 → 계좌 송금 요청
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountTransferRequest {
    private String toAccountNum;   // 상대 계좌번호
    private String toAccountName;  // 상대 계좌주
    private Long amount;           // 송금 금액
}
