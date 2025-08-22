package com.example.hanaharmonybackend.web.controller;

import com.example.hanaharmonybackend.service.AccountService;
import com.example.hanaharmonybackend.web.dto.account.AccountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class AccountController {
  private final AccountService accountService;

  @GetMapping
  public ResponseEntity<AccountResponse> getMyAccount(@AuthenticationPrincipal Long userId){
	return ResponseEntity.ok(accountService.getMyAccount(userId));
  }

}
