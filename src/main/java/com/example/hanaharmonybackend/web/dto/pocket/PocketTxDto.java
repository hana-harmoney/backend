package com.example.hanaharmonybackend.web.dto.pocket;

import com.example.hanaharmonybackend.web.dto.TransactionType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PocketTxDto {
  private Long txId;
  private TransactionType direction;
  private Long amount;
  private LocalDateTime createdAt;

  private Long fromPocketId;
  private Long fromAccountId;
  private Long toPocketId;
  private Long toAccountId;

  private String fromName;
  private String toName;
}
