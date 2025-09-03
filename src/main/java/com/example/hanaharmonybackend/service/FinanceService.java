package com.example.hanaharmonybackend.service;

import com.example.hanaharmonybackend.web.dto.ExpenseResponse;
import com.example.hanaharmonybackend.web.dto.IncomeResponse;

public interface FinanceService {
    IncomeResponse getMonthlyIncome(Integer month);
    ExpenseResponse getMonthlyExpense(Integer month);

}
