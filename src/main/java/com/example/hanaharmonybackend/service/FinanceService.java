package com.example.hanaharmonybackend.service;

import com.example.hanaharmonybackend.web.dto.finance.ExpenseResponse;
import com.example.hanaharmonybackend.web.dto.finance.IncomeResponse;

public interface FinanceService {
    IncomeResponse getMonthlyIncome(Integer month);
    ExpenseResponse getMonthlyExpense(Integer month);

}
