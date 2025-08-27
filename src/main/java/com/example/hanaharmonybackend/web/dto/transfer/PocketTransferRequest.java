package com.example.hanaharmonybackend.web.dto.transfer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 주머니 <-> 계좌 요청
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PocketTransferRequest {
    private Long amount; // 이체 금액
}
