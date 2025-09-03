package com.example.hanaharmonybackend.repository;

import com.example.hanaharmonybackend.domain.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    boolean existsByAccount_AccountIdAndMonth(Long accountId, Integer month);
    Optional<Expense> findByAccount_User_IdAndMonth(Long userId, Integer month);

}