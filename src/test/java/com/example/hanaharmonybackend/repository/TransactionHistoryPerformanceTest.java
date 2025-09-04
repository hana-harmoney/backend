package com.example.hanaharmonybackend.repository;

import com.example.hanaharmonybackend.domain.TransactionHistory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class TransactionHistoryPerformanceTest {

    @Autowired
    private TransactionHistoryRepository txRepository;

    @DisplayName("주머니 거래내역 조회 성능 테스트 JPQL vs QueryDsl")
    @Test
    void testFindByPocketIdPerformance() {
        Long pocketId = 1L; // 테스트용 포켓 ID

        // JPQL 성능 측정
        long startJPQL = System.currentTimeMillis();
        List<TransactionHistory> jpqlRows = txRepository.findByPocketId(pocketId);
        long jpqlTime = System.currentTimeMillis() - startJPQL;

        // QueryDSL 성능 측정
        long startDSL = System.currentTimeMillis();
        List<TransactionHistory> dslRows = txRepository.findByPocketIdDsl(pocketId);
        long dslTime = System.currentTimeMillis() - startDSL;

        System.out.printf("[PERF-Pocket] JPQL: %d ms, rows=%d%n", jpqlTime, jpqlRows.size());
        System.out.printf("[PERF-Pocket] QueryDSL: %d ms, rows=%d%n", dslTime, dslRows.size());
    }

    @DisplayName("계좌 거래내역 조회 테스트 JPQL vs QueryDsl")
    @Test
    void testFindAccountHistoryPerformance() {
        Long accountId = 1L;

        // JPQL 성능 측정
        long startJPQL = System.currentTimeMillis();
        List<TransactionHistory> jpqlRows = txRepository.findAccountHistory(accountId);
        long jpqlTime = System.currentTimeMillis() - startJPQL;

        // QueryDSL 성능 측정
        long startDSL = System.currentTimeMillis();
        List<TransactionHistory> dslRows = txRepository.findAccountHistoryDsl(accountId);
        long dslTime = System.currentTimeMillis() - startDSL;

        System.out.printf("[PERF-Account] JPQL: %d ms, rows=%d%n", jpqlTime, jpqlRows.size());
        System.out.printf("[PERF-Account] QueryDSL: %d ms, rows=%d%n", dslTime, dslRows.size());
    }
}