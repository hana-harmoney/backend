package com.example.hanaharmonybackend.repository;

import com.example.hanaharmonybackend.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("SELECT a.user.name FROM Account a " +
            "WHERE a.accountNum = :accountNum AND a.isDeleted = false")
    Optional<String> findOwnerNameByAccountNum(@Param("accountNum") String accountNum);
}
