package com.example.hanaharmonybackend.service.serviceImpl;

import com.example.hanaharmonybackend.domain.Expense;
import com.example.hanaharmonybackend.domain.Income;
import com.example.hanaharmonybackend.payload.code.ErrorStatus;
import com.example.hanaharmonybackend.payload.exception.CustomException;
import com.example.hanaharmonybackend.repository.ExpenseRepository;
import com.example.hanaharmonybackend.repository.IncomeRepository;
import com.example.hanaharmonybackend.service.FinanceService;
import com.example.hanaharmonybackend.util.SecurityUtil;
import com.example.hanaharmonybackend.web.dto.finance.ExpenseResponse;
import com.example.hanaharmonybackend.web.dto.finance.IncomeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FinanceServiceImpl implements FinanceService {
    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;

    private Long currentUserId() {
        return SecurityUtil.getCurrentMember().getId();
    }

    @Override
    public IncomeResponse getMonthlyIncome(Integer month) {
        Long userId = currentUserId();
        int m = month;

        Income income = incomeRepository.findByAccount_User_IdAndMonth(userId, m)
                .orElseThrow(() -> new CustomException(ErrorStatus.FINANCE_RECORD_NOT_FOUND)); // 필요 시 전용 코드 추가

        long total = n(income.getPension()) + n(income.getRentIncome())
                + n(income.getHarmoneyIncome()) + n(income.getOtherIncome());

        return IncomeResponse.builder()
                .month(m)
                .pension(n(income.getPension()))
                .rentIncome(n(income.getRentIncome()))
                .harmoneyIncome(n(income.getHarmoneyIncome()))
                .otherIncome(n(income.getOtherIncome()))
                .totalIncome(total)
                .build();
    }

    @Override
    public ExpenseResponse getMonthlyExpense(Integer month) {
        Long userId = currentUserId();
        int m = month;

        Expense expense = expenseRepository.findByAccount_User_IdAndMonth(userId, m)
                .orElseThrow(() -> new CustomException(ErrorStatus.FINANCE_RECORD_NOT_FOUND));

        long total = n(expense.getLivingExpense()) + n(expense.getMedicalExpense())
                + n(expense.getLeisureExpense()) + n(expense.getOtherExpense());

        return ExpenseResponse.builder()
                .month(m)
                .livingExpense(n(expense.getLivingExpense()))
                .medicalExpense(n(expense.getMedicalExpense()))
                .leisureExpense(n(expense.getLeisureExpense()))
                .otherExpense(n(expense.getOtherExpense()))
                .totalExpense(total)
                .build();
    }

    private long n(Long v) { return v == null ? 0L : v; }

}
