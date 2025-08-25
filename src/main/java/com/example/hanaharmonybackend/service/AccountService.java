package com.example.hanaharmonybackend.service;

import com.example.hanaharmonybackend.web.dto.AccountNameResponse;
import com.example.hanaharmonybackend.web.dto.account.AccountResponse;

public interface AccountService {
  AccountResponse getMyAccount(Long userId);
  AccountNameResponse getAccountOwnerName(String accountNum);
}