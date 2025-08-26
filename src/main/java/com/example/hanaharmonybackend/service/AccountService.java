package com.example.hanaharmonybackend.service;

import com.example.hanaharmonybackend.web.dto.account.AccountDetailResponse;
import com.example.hanaharmonybackend.web.dto.account.AccountResponse;

public interface AccountService {
  AccountResponse getMyAccount(Long userId);
  AccountDetailResponse getDetail(Long requesterId, Long accountId);
}