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
        LEFT JOIN FETCH t.fromAccount  fa
        LEFT JOIN FETCH fa.user        fu
        LEFT JOIN FETCH t.toAccount    ta
        LEFT JOIN FETCH ta.user        tu
        LEFT JOIN FETCH t.fromPocket   fp
        LEFT JOIN FETCH fp.account     fpa
        LEFT JOIN FETCH fpa.user       fpu
        LEFT JOIN FETCH t.toPocket     tp
        LEFT JOIN FETCH tp.account     tpa
        LEFT JOIN FETCH tpa.user       tpu
      WHERE
           (fa.accountId  = :accountId)
        OR (ta.accountId  = :accountId)
        OR (fpa.accountId = :accountId)
        OR (tpa.accountId = :accountId)
      ORDER BY t.createdAt DESC
    """)
  List<TransactionHistory> findAccountHistory(@Param("accountId") Long accountId);
}
