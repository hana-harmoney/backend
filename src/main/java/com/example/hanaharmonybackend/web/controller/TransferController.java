package com.example.hanaharmonybackend.web.controller;

import com.example.hanaharmonybackend.payload.ApiResponse;
import com.example.hanaharmonybackend.service.AccountService;
import com.example.hanaharmonybackend.service.TransferService;
import com.example.hanaharmonybackend.util.SecurityUtil;
import com.example.hanaharmonybackend.web.dto.AccountNameRequest;
import com.example.hanaharmonybackend.web.dto.AccountNameResponse;
import com.example.hanaharmonybackend.web.dto.TransferData;
import com.example.hanaharmonybackend.web.dto.TransferRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transfer")
@RequiredArgsConstructor
public class TransferController {

    private final AccountService accountService;
    private final TransferService transferService;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> transfer(@RequestBody TransferRequest request) {
        Long userId = SecurityUtil.getCurrentMember().getId();
        TransferData response = transferService.transfer(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/name")
    public ResponseEntity<ApiResponse<?>> getAccountOwner(@RequestBody AccountNameRequest request) {
        AccountNameResponse response = accountService.getAccountOwnerName(request.getAccount_num());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
