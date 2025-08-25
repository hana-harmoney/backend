package com.example.hanaharmonybackend.service;

import com.example.hanaharmonybackend.web.dto.AccountNameResponse;

public interface AccountService {
    AccountNameResponse getAccountOwnerName(String accountNum);
}

