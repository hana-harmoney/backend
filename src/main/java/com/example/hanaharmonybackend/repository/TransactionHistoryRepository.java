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

  @Query("""
  SELECT t FROM TransactionHistory t
    LEFT JOIN FETCH t.fromAccount fa
    LEFT JOIN FETCH fa.user        fu
    LEFT JOIN FETCH t.toAccount   ta
    LEFT JOIN FETCH ta.user       tu
    LEFT JOIN FETCH t.fromPocket  fp
    LEFT JOIN FETCH t.toPocket    tp
  WHERE (fa.accountId = :accountId)
     OR (ta.accountId = :accountId)
     OR (fp.account.accountId = :accountId)
     OR (tp.account.accountId = :accountId)
  ORDER BY t.createdAt DESC
""")
  List<TransactionHistory> findAccountHistory(@Param("accountId") Long accountId);
}
