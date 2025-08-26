package com.example.hanaharmonybackend.web.controller;

import com.example.hanaharmonybackend.domain.User;
import com.example.hanaharmonybackend.payload.ApiResponse;
import com.example.hanaharmonybackend.service.AccountService;
import com.example.hanaharmonybackend.util.SecurityUtil;
import com.example.hanaharmonybackend.web.dto.account.AccountDetailResponse;
import com.example.hanaharmonybackend.web.dto.account.AccountResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Account", description = "계좌 조회 API")
@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class AccountController {
  private final AccountService accountService;

  @GetMapping
  public ApiResponse<AccountResponse> getMyAccount(){
	User user = SecurityUtil.getCurrentMember();
	return ApiResponse.success(accountService.getMyAccount(user.getId()));
  }

  @GetMapping("/history")
  public ApiResponse<AccountDetailResponse> detail(@RequestParam Long accountId){
	Long userId = SecurityUtil.getCurrentMember().getId();
	AccountDetailResponse res = accountService.getDetail(userId, accountId);
	return ApiResponse.success(res);
  }

}
