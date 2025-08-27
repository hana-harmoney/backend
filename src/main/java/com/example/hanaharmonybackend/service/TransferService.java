package com.example.hanaharmonybackend.service;

import com.example.hanaharmonybackend.web.dto.transfer.*;

public interface TransferService {
    AccountTransferResponse transferAccountToAccount(AccountTransferRequest request);
    PocketTransferResponse transferAccountToPocket(Long pocketId, PocketTransferRequest request);
    PocketTransferResponse transferPocketToAccount(Long pocketId, PocketTransferRequest request);
}

