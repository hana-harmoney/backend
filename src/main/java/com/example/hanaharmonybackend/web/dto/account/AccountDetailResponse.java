package com.example.hanaharmonybackend.web.dto.account;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AccountDetailResponse {
  private Long accountId;
  private String accountNum;
  private String ownerName;
  private Long accountBalance;

  private List<AccountTxDto> history; // 최신순
}
