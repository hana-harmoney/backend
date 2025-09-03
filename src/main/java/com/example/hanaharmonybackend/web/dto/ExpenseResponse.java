package com.example.hanaharmonybackend.web.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExpenseResponse {
    private Integer month;
    private Long livingExpense;       // 생활비
    private Long medicalExpense;      // 의료비
    private Long leisureExpense;      // 여가비
    private Long otherExpense;        // 기타
    private Long totalExpense;        // 합계
}
