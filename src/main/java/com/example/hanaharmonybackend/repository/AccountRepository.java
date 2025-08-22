package com.example.hanaharmonybackend.repository;

import com.example.hanaharmonybackend.domain.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
  // user_id 로 단건 조회 (pockets까지 즉시 로딩)
  @EntityGraph(attributePaths = "pockets")
  Optional<Account> findByUser_Id(Long userId);
}
