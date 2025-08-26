package com.example.hanaharmonybackend.web.dto.pocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PocketCreateResponse {
  private String pocketName;
  private Long targetAmount;
  private Long currentAmount;
}