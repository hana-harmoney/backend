package com.example.hanaharmonybackend.service.serviceImpl;

import com.example.hanaharmonybackend.domain.Account;
import com.example.hanaharmonybackend.payload.code.ErrorStatus;
import com.example.hanaharmonybackend.payload.exception.CustomException;
import com.example.hanaharmonybackend.repository.AccountRepository;
import com.example.hanaharmonybackend.service.TransferService;
import com.example.hanaharmonybackend.web.dto.TransferData;
import com.example.hanaharmonybackend.web.dto.TransferRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final AccountRepository accountRepository;

    @Transactional
    @Override
    public TransferData transfer(Long userId, TransferRequest request) {
        // 내 계좌 조회 (유저 1 : 계좌 1)
        Account myAccount = accountRepository.findByUser_Id(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.ACCOUNT_NOT_FOUND));

        // 상대 계좌 조회
        Account toAccount = accountRepository.findByAccountNum(request.getToAccountNum())
                .orElseThrow(() -> new CustomException(ErrorStatus.ACCOUNT_NOT_FOUND));

        // 잔액 검증 후 송금
        myAccount.withdraw(request.getAmount());
        toAccount.deposit(request.getAmount());

        return new TransferData(
                myAccount.getAccountBalance(),
                toAccount.getAccountNum(),
                request.getToAccountName(),
                request.getAmount()
        );
    }
}

