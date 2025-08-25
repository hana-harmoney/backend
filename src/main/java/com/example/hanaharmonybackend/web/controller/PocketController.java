package com.example.hanaharmonybackend.web.controller;

import com.example.hanaharmonybackend.domain.Account;
import com.example.hanaharmonybackend.payload.ApiResponse;
import com.example.hanaharmonybackend.payload.code.ErrorStatus;
import com.example.hanaharmonybackend.payload.exception.CustomException;
import com.example.hanaharmonybackend.repository.AccountRepository;
import com.example.hanaharmonybackend.service.PocketCommandService;
import com.example.hanaharmonybackend.util.SecurityUtil;
import com.example.hanaharmonybackend.web.dto.pocket.PocketCreateRequest;
import com.example.hanaharmonybackend.web.dto.pocket.PocketCreateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Pocket", description = "포켓 생성/삭제 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/home/pocket")
public class PocketController {

  private final PocketCommandService pocketCommandService;
  private final AccountRepository accountRepository;

  @Operation(summary = "포켓 생성", description = "로그인 사용자의 계좌에 새 포켓을 생성합니다.")
  @PostMapping
  public ApiResponse<PocketCreateResponse> create(
	  @Valid @RequestBody PocketCreateRequest req
  ){
	Long userId = SecurityUtil.getCurrentMember().getId();

	Account account = accountRepository.findByUser_Id(userId)
		.orElseThrow(() -> new CustomException(ErrorStatus.ACCOUNT_NOT_FOUND));

	Long accountId = account.getAccountId();

	PocketCreateResponse pocket = pocketCommandService.create(accountId, req, userId);

	return ApiResponse.success(pocket);
  }
}
