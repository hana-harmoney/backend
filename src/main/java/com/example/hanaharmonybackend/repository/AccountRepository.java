package com.example.hanaharmonybackend.repository;

import com.example.hanaharmonybackend.domain.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
  @EntityGraph(attributePaths = "pockets")
  Optional<Account> findByUser_IdAndDeletedFalse(Long userId);

  boolean existsByAccountNum(String accountNum);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("update Account a set a.deleted = true where a.user.id = :userId and a.deleted = false")
  int softDeleteByUserId(@Param("userId") Long userId);
}
