package com.example.hanaharmonybackend.web.dto.account;

import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class AccountResponse {
  private final Long totalAssets;
  private final String account;
  private final List<PocketDto> pocketLists;

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class PocketDto {
	private String name;
	private Long amount;
  }
}
