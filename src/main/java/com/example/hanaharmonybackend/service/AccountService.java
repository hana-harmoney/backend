package com.example.hanaharmonybackend.service;

import com.example.hanaharmonybackend.domain.Account;
import com.example.hanaharmonybackend.repository.AccountRepository;
import com.example.hanaharmonybackend.web.dto.account.AccountResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {
  AccountRepository accountRepository;

  @Transactional(readOnly = true)
  public AccountResponse getMyAccount(Long userId) {
    Account account = accountRepository.findByUser_Id(userId)
        .orElseThrow(() -> new IllegalArgumentException("해당 사용자 계좌가 없습니다. userId=" + userId));

    long totalAssets = account.getPockets().stream()
        .mapToLong(p -> p.getCurrentAmount() == null ? 0L : p.getCurrentAmount())
        .sum();

    return AccountResponse.builder()
        .totalAssets(totalAssets)
        .account(account.getAccountNum())
        .pocketLists(account.getPockets().stream()
            .map(p->AccountResponse.PocketDto.builder()
                .name(p.getPocketName())
                .amount(p.getCurrentAmount())
                .build())
            .toList()
        )
        .build();
  }
}

