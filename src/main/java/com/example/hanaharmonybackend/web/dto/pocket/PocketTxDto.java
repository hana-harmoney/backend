package com.example.hanaharmonybackend.web.dto.pocket;

import lombok.Builder;
import lombok.Getter;
import com.fasterxml.jackson.annotation.JsonValue;

import java.time.LocalDateTime;

@Getter
@Builder
public class PocketTxDto {
  public enum Direction {
    IN("입금"),
    OUT("출금");

    private final String label;

    Direction(String label) {
      this.label = label;
    }

    @JsonValue
    public String getLabel() {
      return label;
    }
  }

  private Long txId;
  private Direction direction;
  private Long amount;
  private LocalDateTime createdAt;

  private Long fromPocketId;
  private Long fromAccountId;
  private Long toPocketId;
  private Long toAccountId;

  private String fromName;
  private String toName;
}
