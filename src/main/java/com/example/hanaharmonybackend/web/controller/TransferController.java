package com.example.hanaharmonybackend.web.controller;

import com.example.hanaharmonybackend.payload.ApiResponse;
import com.example.hanaharmonybackend.service.AccountService;
import com.example.hanaharmonybackend.service.TransferService;
import com.example.hanaharmonybackend.web.dto.account.AccountNameRequest;
import com.example.hanaharmonybackend.web.dto.account.AccountNameResponse;
import com.example.hanaharmonybackend.web.dto.transfer.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Transfer", description = "송금 API")
@RestController
@RequestMapping("/transfer")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;
    private final AccountService accountService;

    // 계좌 → 계좌 송금
    @Operation(summary = "계좌 간 송금")
    @PostMapping
    public ResponseEntity<ApiResponse<AccountTransferResponse>> transferAccountToAccount(
            @RequestBody AccountTransferRequest request
    ) {
        AccountTransferResponse response = transferService.transferAccountToAccount(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 계좌 → 주머니
    @Operation(summary = "주머니로 채우기")
    @PatchMapping("/{pocketId}/plus")
    public ResponseEntity<ApiResponse<PocketTransferResponse>> transferAccountToPocket(
            @PathVariable Long pocketId,
            @RequestBody PocketTransferRequest request
    ) {
        PocketTransferResponse response = transferService.transferAccountToPocket(pocketId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 주머니 → 계좌
    @Operation(summary = "주머니에서 꺼내기")
    @PatchMapping("/{pocketId}/minus")
    public ResponseEntity<ApiResponse<PocketTransferResponse>> transferPocketToAccount(
            @PathVariable Long pocketId,
            @RequestBody PocketTransferRequest request
    ) {
        PocketTransferResponse response = transferService.transferPocketToAccount(pocketId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "계좌번호로 예금주 조회")
    @PostMapping("/name")
    public ResponseEntity<ApiResponse<?>> getAccountOwner(@RequestBody AccountNameRequest request) {
        AccountNameResponse response = accountService.getAccountOwnerName(request.getAccount_num());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
