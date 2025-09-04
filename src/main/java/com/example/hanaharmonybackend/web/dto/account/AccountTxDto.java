package com.example.hanaharmonybackend.web.dto.account;

import com.example.hanaharmonybackend.web.dto.TransactionType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccountTxDto {

  private Long transactionId;
  private TransactionType transactionType;

  private String fromAccountNum;
  private String fromAccountName;
  private String toAccountNum;
  private String toAccountName;

  private Long fromPocketId;
  private String fromPocketName;
  private Long toPocketId;
  private String toPocketName;

  private Long amount;
  private String day;
  private String time;
}
