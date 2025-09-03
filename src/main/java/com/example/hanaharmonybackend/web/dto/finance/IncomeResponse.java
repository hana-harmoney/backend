package com.example.hanaharmonybackend.web.dto.finance;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IncomeResponse {
    private Integer month;
    private Long pension;        // 연금
    private Long rentIncome;     // 임대소득
    private Long harmoneyIncome; // 하모니 수입
    private Long otherIncome;    // 기타
    private Long totalIncome;    // 합계
}
