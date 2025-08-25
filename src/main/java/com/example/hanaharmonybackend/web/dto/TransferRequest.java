package com.example.hanaharmonybackend.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequest {
    private String toAccountNum;   // 상대 계좌번호
    private String toAccountName;  // 상대 계좌주
    private Long amount;           // 송금 금액
}
