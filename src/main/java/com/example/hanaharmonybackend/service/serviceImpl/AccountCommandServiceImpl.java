package com.example.hanaharmonybackend.service.serviceImpl;

import com.example.hanaharmonybackend.domain.Account;
import com.example.hanaharmonybackend.domain.User;
import com.example.hanaharmonybackend.payload.code.ErrorStatus;
import com.example.hanaharmonybackend.payload.exception.CustomException;
import com.example.hanaharmonybackend.repository.AccountRepository;
import com.example.hanaharmonybackend.service.AccountCommandService;
import com.example.hanaharmonybackend.util.AccountNumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountCommandServiceImpl implements AccountCommandService {

  private final AccountRepository accountRepository;
  private final AccountNumberGenerator generator;

  @Override
  @Transactional
  public Account createFor(User user) {
	for (int i=0; i<3; i++){
	  String num = generator.generateUnique();
	  var acc = Account.builder()
		  .accountNum(num).accountBalance(0L).deleted(false).user(user).build();
	  try {
		return accountRepository.saveAndFlush(acc);
	  } catch (DataIntegrityViolationException e) {/* retry */}
	}
	throw new IllegalStateException("계좌 생성 중 중복 충돌이 반복되었습니다.");
  }

  @Override
  public void delete(Long accountId, Long requesterId) {
	var acc = accountRepository.findById(accountId)
		.orElseThrow(() -> new CustomException(ErrorStatus.ACCOUNT_NOT_FOUND));

	if (!acc.getUser().getId().equals(requesterId)) {
	  throw new CustomException(ErrorStatus.ACCOUNT_ACCESS_DENIED);
	}
	acc.setDeleted(true);
  }
}
