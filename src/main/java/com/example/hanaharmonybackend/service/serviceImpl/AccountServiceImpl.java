package com.example.hanaharmonybackend.service.serviceImpl;

import com.example.hanaharmonybackend.domain.Account;
import com.example.hanaharmonybackend.domain.TransactionHistory;
import com.example.hanaharmonybackend.payload.code.ErrorStatus;
import com.example.hanaharmonybackend.payload.exception.CustomException;
import com.example.hanaharmonybackend.repository.AccountRepository;
import com.example.hanaharmonybackend.repository.TransactionHistoryRepository;
import com.example.hanaharmonybackend.service.AccountService;
import com.example.hanaharmonybackend.web.dto.TransactionType;
import com.example.hanaharmonybackend.web.dto.account.AccountDetailResponse;
import com.example.hanaharmonybackend.web.dto.account.AccountResponse;
import com.example.hanaharmonybackend.web.dto.account.AccountTxDto;
import com.example.hanaharmonybackend.web.dto.AccountNameResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountServiceImpl implements AccountService {

  private final AccountRepository accountRepository;
  private final TransactionHistoryRepository txRepository;

  @Override
  public AccountResponse getMyAccount(Long userId) {
    Account account = accountRepository.findByUser_IdAndDeletedFalse(userId)
        .orElseThrow(() -> new CustomException(ErrorStatus.ACCOUNT_NOT_FOUND));

    long pocketsSum = account.getPockets().stream()
        .mapToLong(p -> p.getCurrentAmount() == null ? 0L : p.getCurrentAmount())
        .sum();

    long totalAssets = account.getAccountBalance() + pocketsSum;

    return AccountResponse.builder()
        .totalAssets(totalAssets)
        .account(account.getAccountNum())
        .accountId(account.getAccountId())
        .accountBalance(account.getAccountBalance())
        .pocketLists(account.getPockets().stream()
            .map(p->AccountResponse.PocketDto.builder()
                    .pocketId(p.getPocketId())
                .name(p.getPocketName())
                .amount(p.getCurrentAmount())
                .build())
            .toList()
        )
        .build();
  }

  @Override
  public AccountDetailResponse getDetail(Long requesterId, Long accountId) {
    Account account = accountRepository.findByAccountIdAndDeletedFalse(accountId)
        .orElseThrow(() -> new CustomException(ErrorStatus.ACCOUNT_NOT_FOUND));

    if (!account.getUser().getId().equals(requesterId)) {
      throw new CustomException(ErrorStatus.ACCOUNT_ACCESS_DENIED);
    }

    List<TransactionHistory> rows = txRepository.findAccountHistory(accountId);

    List<AccountTxDto> history = rows.stream().map(tx -> {

      // 내 계좌번호 기준으로 입/출금 판정
      final String myAccountNum = account.getAccountNum();
      final String fromAccNum = tx.getFromAccount() != null ? tx.getFromAccount().getAccountNum() : null;
      final String toAccNum   = tx.getToAccount()   != null ? tx.getToAccount().getAccountNum()   : null;

      TransactionType txType = null;
      if (myAccountNum != null && myAccountNum.equals(fromAccNum)) {
        txType = TransactionType.OUT; // 출금
      } else if (myAccountNum != null && myAccountNum.equals(toAccNum)) {
        txType = TransactionType.IN;  // 입금
      }

      String fromAccountNum   = null;
      String fromAccountName  = null;
      if (tx.getFromAccount() != null) {
        fromAccountNum  = tx.getFromAccount().getAccountNum();
        fromAccountName = tx.getFromAccount().getUser().getName();
      }

      String toAccountNum   = null;
      String toAccountName  = null;
      if (tx.getToAccount() != null) {
        toAccountNum  = tx.getToAccount().getAccountNum();
        toAccountName = tx.getToAccount().getUser().getName();
      }

      Long   fromPocketId    = null;
      String fromPocketName  = null;
      if (tx.getFromPocket() != null) {
        fromPocketId   = tx.getFromPocket().getPocketId();
        fromPocketName = tx.getFromPocket().getPocketName();
      }

      Long   toPocketId    = null;
      String toPocketName  = null;
      if (tx.getToPocket() != null) {
        toPocketId   = tx.getToPocket().getPocketId();
        toPocketName = tx.getToPocket().getPocketName();
      }

      return AccountTxDto.builder()
          .transactionId(tx.getId())
          .transactionType(txType)
          .fromAccountNum(fromAccountNum)
          .fromAccountName(fromAccountName)
          .toAccountNum(toAccountNum)
          .toAccountName(toAccountName)
          .fromPocketId(fromPocketId)
          .fromPocketName(fromPocketName)
          .toPocketId(toPocketId)
          .toPocketName(toPocketName)
          .amount(tx.getAmount())
          .day(tx.getCreatedAt().toLocalDate().toString())
          .time(tx.getCreatedAt().toLocalTime().toString())
          .build();
    }).toList();

    return AccountDetailResponse.builder()
        .accountId(account.getAccountId())
        .accountNum(account.getAccountNum())
        .ownerName(account.getUser().getName())
        .accountBalance(account.getAccountBalance())
        .history(history)
        .build();
  }

  @Override
  public AccountNameResponse getAccountOwnerName(String accountNum) {
    String ownerName = accountRepository.findOwnerNameByAccountNum(accountNum)
            .orElseThrow(() -> new CustomException(ErrorStatus.ACCOUNT_NOT_FOUND));

    return AccountNameResponse.builder()
            .name(ownerName)
            .build();
  }
}

