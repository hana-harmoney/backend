package com.example.hanaharmonybackend.repository.repositoryImpl;

import com.example.hanaharmonybackend.domain.TransactionHistory;

import java.util.List;

public interface TransactionHistoryRepositoryCustom {
    List<TransactionHistory> findByPocketIdDsl(Long pocketId);
    List<TransactionHistory> findAccountHistoryDsl(Long accountId);
}
