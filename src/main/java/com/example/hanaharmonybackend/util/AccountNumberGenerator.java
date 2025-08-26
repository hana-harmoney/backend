package com.example.hanaharmonybackend.util;

import com.example.hanaharmonybackend.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
public class AccountNumberGenerator {
  private final AccountRepository accountRepository;
  private static final SecureRandom RANDOM = new SecureRandom();

  // 예: 3-4-4 형식 "123-4567-8901"
  public String generateUnique() {
	for (int i = 0; i < 20; i++) {
	  String candidate = String.format("%03d-%06d-%05d",
		  RANDOM.nextInt(1000), RANDOM.nextInt(1000000), RANDOM.nextInt(100000));
	  if (!accountRepository.existsByAccountNum(candidate)) return candidate;
	}
	throw new IllegalStateException("계좌번호 생성에 실패했습니다.");
  }
}
