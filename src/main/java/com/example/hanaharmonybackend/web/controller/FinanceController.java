package com.example.hanaharmonybackend.web.controller;

import com.example.hanaharmonybackend.payload.ApiResponse;
import com.example.hanaharmonybackend.service.FinanceService;
import com.example.hanaharmonybackend.web.dto.finance.ExpenseResponse;
import com.example.hanaharmonybackend.web.dto.finance.IncomeResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/finance")
public class FinanceController {

    private final FinanceService financeService;

    @Operation(summary = "월별 수입 단건 조회 (month=1~12 필수)")
    @GetMapping("/income")
    public ResponseEntity<ApiResponse<IncomeResponse>> getIncome(
            @RequestParam("month") @Min(1) @Max(12) Integer month
    ) {
        IncomeResponse data = financeService.getMonthlyIncome(month);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @Operation(summary = "월별 지출 단건 조회 (month=1~12 필수)")
    @GetMapping("/expense")
    public ResponseEntity<ApiResponse<ExpenseResponse>> getExpense(
            @RequestParam("month") @Min(1) @Max(12) Integer month
    ) {
        ExpenseResponse data = financeService.getMonthlyExpense(month);
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
