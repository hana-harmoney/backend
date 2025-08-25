package com.example.hanaharmonybackend.service;

import com.example.hanaharmonybackend.domain.Account;
import com.example.hanaharmonybackend.payload.code.ErrorStatus;
import com.example.hanaharmonybackend.payload.exception.CustomException;
import com.example.hanaharmonybackend.repository.AccountRepository;
import com.example.hanaharmonybackend.web.dto.account.AccountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {
  private final AccountRepository accountRepository;

  @Transactional(readOnly = true)
  public AccountResponse getMyAccount(Long userId) {
    Account account = accountRepository.findByUser_Id(userId)
        .orElseThrow(() -> new CustomException(ErrorStatus.ACCOUNT_NOT_FOUND));

    long pocketsSum = account.getPockets().stream()
        .mapToLong(p -> p.getCurrentAmount() == null ? 0L : p.getCurrentAmount())
        .sum();

    long totalAssets = account.getAccountBalance() + pocketsSum;

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

