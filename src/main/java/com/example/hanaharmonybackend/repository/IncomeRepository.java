package com.example.hanaharmonybackend.repository;

import com.example.hanaharmonybackend.domain.Income;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncomeRepository extends JpaRepository<Income, Long> {
    boolean existsByAccount_AccountIdAndMonth(Long accountId, Integer month);
}