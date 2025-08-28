package com.example.hanaharmonybackend.repository;

import com.example.hanaharmonybackend.domain.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
  @EntityGraph(attributePaths = "pockets")
  Optional<Account> findByUser_IdAndDeletedFalse(Long userId);

  Optional<Account> findByAccountIdAndDeletedFalse(Long id);

  boolean existsByAccountNum(String accountNum);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("update Account a set a.deleted = true where a.user.id = :userId and a.deleted = false")
  int softDeleteByUserId(@Param("userId") Long userId);
    // 계좌 번호 입력시, 예금주 확인 가능
    @Query("SELECT a.user.name FROM Account a " +
            "WHERE a.accountNum = :accountNum AND a.deleted = false")
    Optional<String> findOwnerNameByAccountNum(@Param("accountNum") String accountNum);

    // 계좌가 존재하는지 확인하기 위한 계좌 조회
    Optional<Account> findByAccountNum(String accountNum);

    // 로그인한 유저의 계좌 찾기 (기본 계좌 1개만 존재)
    Optional<Account> findByUser_Id(Long userId);

    // 계좌번호 + 사용자 이름으로 계좌 찾기
    Optional<Account> findByAccountNumAndUser_Name(String accountNum, String userName);
}
