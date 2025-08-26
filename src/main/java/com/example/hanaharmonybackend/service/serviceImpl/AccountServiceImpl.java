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

  @Override
  public AccountDetailResponse getDetail(Long requesterId, Long accountId) {
    Account account = accountRepository.findById(accountId)
        .orElseThrow(() -> new CustomException(ErrorStatus.ACCOUNT_NOT_FOUND));

    if (!account.getUser().getId().equals(requesterId)) {
      throw new CustomException(ErrorStatus.ACCOUNT_ACCESS_DENIED);
    }

    List<TransactionHistory> rows = txRepository.findAccountHistory(accountId);

    List<AccountTxDto> history = rows.stream().map(tx -> {
      final String myAccountNum = account.getAccountNum();

      final String fromAccountNum = tx.getFromAccount() != null ? tx.getFromAccount().getAccountNum() : null;
      final String toAccountNum   = tx.getToAccount()   != null ? tx.getToAccount().getAccountNum()   : null;

      boolean isOut = myAccountNum != null && myAccountNum.equals(fromAccountNum);
      TransactionType txType = isOut ? TransactionType.OUT : TransactionType.IN;

      String fromName =
          tx.getFromPocket()  != null ? tx.getFromPocket().getAccount().getUser().getName()
              : tx.getFromAccount() != null ? tx.getFromAccount().getUser().getName()
              : null;

      String toName =
          tx.getToPocket()  != null ? tx.getToPocket().getAccount().getUser().getName()
              : tx.getToAccount() != null ? tx.getToAccount().getUser().getName()
              : null;

      Long   fromPocketId    = tx.getFromPocket()  != null ? tx.getFromPocket().getPocketId()   : null;
      String fromPocketName  = tx.getFromPocket()  != null ? tx.getFromPocket().getPocketName() : null;
      Long   toPocketId      = tx.getToPocket()    != null ? tx.getToPocket().getPocketId()     : null;
      String toPocketName    = tx.getToPocket()    != null ? tx.getToPocket().getPocketName()   : null;

      return AccountTxDto.builder()
          .transactionId(tx.getId())
          .transactionType(txType)
          .fromAccountNum(fromAccountNum)
          .fromAccountName(fromName)
          .toAccountName(toAccountNum)
          .toAccountName(toName)
          .fromPocketId(fromPocketId)
          .fromPocketName(fromPocketName)
          .toPocketId(toPocketId)
          .toPocketName(toPocketName)
          .amount(tx.getAmount())
          .createdAt(tx.getCreatedAt())
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
}

