package com.example.hanaharmonybackend.service;

import com.example.hanaharmonybackend.web.dto.TransferData;
import com.example.hanaharmonybackend.web.dto.TransferRequest;

public interface TransferService {
    TransferData transfer(Long myAccountId, TransferRequest request);
}
