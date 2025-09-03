package com.example.hanaharmonybackend.repository;

import com.example.hanaharmonybackend.domain.Income;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IncomeRepository extends JpaRepository<Income, Long> {
    boolean existsByAccount_AccountIdAndMonth(Long accountId, Integer month);
    Optional<Income> findByAccount_User_IdAndMonth(Long userId, Integer month);

}