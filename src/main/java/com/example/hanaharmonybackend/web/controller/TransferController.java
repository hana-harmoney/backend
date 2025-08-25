package com.example.hanaharmonybackend.web.controller;

import com.example.hanaharmonybackend.payload.ApiResponse;
import com.example.hanaharmonybackend.service.AccountService;
import com.example.hanaharmonybackend.web.dto.AccountNameRequest;
import com.example.hanaharmonybackend.web.dto.AccountNameResponse;
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

    @PostMapping("/name")
    public ResponseEntity<ApiResponse<?>> getAccountOwner(@RequestBody AccountNameRequest request) {
        AccountNameResponse response = accountService.getAccountOwnerName(request.getAccount_num());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
