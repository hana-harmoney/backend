package com.example.hanaharmonybackend.service.serviceImpl;

import com.example.hanaharmonybackend.payload.code.ErrorStatus;
import com.example.hanaharmonybackend.payload.exception.CustomException;
import com.example.hanaharmonybackend.repository.AccountRepository;
import com.example.hanaharmonybackend.service.AccountService;
import com.example.hanaharmonybackend.web.dto.AccountNameResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    @Transactional(readOnly = true)
    public AccountNameResponse getAccountOwnerName(String accountNum) {
        String ownerName = accountRepository.findOwnerNameByAccountNum(accountNum)
                .orElseThrow(() -> new CustomException(ErrorStatus.ACCOUNT_NOT_FOUND));

        return AccountNameResponse.builder()
                .name(ownerName)
                .build();
    }
}
