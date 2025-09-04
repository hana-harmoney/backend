package com.example.hanaharmonybackend.service;

import com.example.hanaharmonybackend.web.dto.transfer.AccountTransferRequest;
import com.example.hanaharmonybackend.web.dto.transfer.AccountTransferResponse;
import com.example.hanaharmonybackend.web.dto.transfer.PocketTransferRequest;
import com.example.hanaharmonybackend.web.dto.transfer.PocketTransferResponse;

public interface TransferService {
    AccountTransferResponse transferAccountToAccount(AccountTransferRequest request);
    PocketTransferResponse transferAccountToPocket(Long pocketId, PocketTransferRequest request);
    PocketTransferResponse transferPocketToAccount(Long pocketId, PocketTransferRequest request);
}

