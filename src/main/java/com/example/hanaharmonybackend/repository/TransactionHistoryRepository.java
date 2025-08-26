package com.example.hanaharmonybackend.repository;

import com.example.hanaharmonybackend.domain.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {
  @Query("""
        SELECT t FROM TransactionHistory t
        WHERE t.fromPocket.pocketId = :pocketId
           OR t.toPocket.pocketId = :pocketId
        ORDER BY t.createdAt DESC
    """)
  List<TransactionHistory> findByPocketId(@Param("pocketId") Long pocketId);
}
