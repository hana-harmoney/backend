package com.example.hanaharmonybackend.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferData {
    private Long change;           // 내 계좌 잔액
    private String toAccountNum;   // 송금 계좌번호
    private String toAccountName;  // 송금 계좌주
    private Long amount;           // 송금 금액
}
