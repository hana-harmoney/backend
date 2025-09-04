package com.example.hanaharmonybackend.web.dto.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class AccountResponse {
    private final Long totalAssets;
    private final String account;
    private final Long accountId;
    private final Long accountBalance;
    private final List<PocketDto> pocketLists;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PocketDto {
        private Long pocketId;
        private String name;
        private Long amount;
        private Long targetAmount;
    }
}
