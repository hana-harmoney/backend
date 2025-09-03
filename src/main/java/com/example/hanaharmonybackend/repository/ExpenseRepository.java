package com.example.hanaharmonybackend.repository;

import com.example.hanaharmonybackend.domain.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    boolean existsByAccount_AccountIdAndMonth(Long accountId, Integer month);
}