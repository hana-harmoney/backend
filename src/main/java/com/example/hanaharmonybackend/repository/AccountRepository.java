package com.example.hanaharmonybackend.repository;

import com.example.hanaharmonybackend.domain.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

  boolean existsByAccountNum(String accountNum);
    // 계좌 번호 입력시, 예금주 확인 가능
    @Query("SELECT a.user.name FROM Account a " +
            "WHERE a.accountNum = :accountNum AND a.deleted = false")
    Optional<String> findOwnerNameByAccountNum(@Param("accountNum") String accountNum);

    // 계좌가 존재하는지 확인하기 위한 계좌 조회
    Optional<Account> findByAccountNum(String accountNum);

  @EntityGraph(attributePaths = "pockets")
  Optional<Account> findByUser_Id(Long userId);
}
