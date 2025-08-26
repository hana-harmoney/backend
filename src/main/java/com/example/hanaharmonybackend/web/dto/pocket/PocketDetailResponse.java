package com.example.hanaharmonybackend.web.dto.pocket;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PocketDetailResponse {
  private Long pocketId;
  private String pocketName;
  private Long targetAmount;
  private Long currentAmount;
  private List<PocketTxDto> transactions; // 최신순 정렬
}
