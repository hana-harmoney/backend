package com.example.hanaharmonybackend.repository;

import com.example.hanaharmonybackend.domain.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

  boolean existsByAccountNum(String accountNum);

  @EntityGraph(attributePaths = "pockets")
  Optional<Account> findByUser_Id(Long userId);
}
