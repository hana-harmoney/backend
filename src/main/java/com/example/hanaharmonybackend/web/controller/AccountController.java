package com.example.hanaharmonybackend.web.controller;

import com.example.hanaharmonybackend.domain.User;
import com.example.hanaharmonybackend.payload.ApiResponse;
import com.example.hanaharmonybackend.service.AccountService;
import com.example.hanaharmonybackend.util.SecurityUtil;
import com.example.hanaharmonybackend.web.dto.account.AccountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
