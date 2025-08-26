package com.example.hanaharmonybackend.web.dto.pocket;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PocketCreateRequest {
  private String name;
  private Long targetAmount;
  private Long initialAmount = 0L;
}
