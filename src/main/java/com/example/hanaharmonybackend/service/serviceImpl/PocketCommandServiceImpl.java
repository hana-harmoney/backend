package com.example.hanaharmonybackend.service.serviceImpl;

import com.example.hanaharmonybackend.domain.Account;
import com.example.hanaharmonybackend.domain.Pocket;
import com.example.hanaharmonybackend.payload.code.ErrorStatus;
import com.example.hanaharmonybackend.payload.exception.CustomException;
import com.example.hanaharmonybackend.repository.AccountRepository;
import com.example.hanaharmonybackend.repository.PocketRepository;
import com.example.hanaharmonybackend.service.PocketCommandService;
import com.example.hanaharmonybackend.web.dto.pocket.PocketCreateRequest;
import com.example.hanaharmonybackend.web.dto.pocket.PocketCreateResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PocketCommandServiceImpl implements PocketCommandService {
  private final PocketRepository pocketRepository;
  private final AccountRepository accountRepository;

  @Override
  @Transactional
  public PocketCreateResponse create(Long accountId, PocketCreateRequest req, Long requesterId) {
	Account account = accountRepository.findById(accountId)
		.orElseThrow(() -> new CustomException(ErrorStatus.ACCOUNT_NOT_FOUND));

	// 소유자 검증
	if (!account.getUser().getId().equals(requesterId)) {
	  throw new CustomException(ErrorStatus.ACCOUNT_ACCESS_DENIED);
	}

	Pocket pocket = Pocket.builder()
		.pocketName(req.getName())
		.targetAmount(req.getTargetAmount())
		.currentAmount(req.getInitialAmount())
		.account(account)
		.build();

	Pocket p = pocketRepository.save(pocket);

	return PocketCreateResponse.builder()
		.pocketName(p.getPocketName())
		.targetAmount(p.getTargetAmount())
		.currentAmount(p.getCurrentAmount())
		.build();
  }

  @Override
  @Transactional
  public void delete(Long pocketId, Long requesterId) {
	Pocket pocket = pocketRepository.findById(pocketId)
		.orElseThrow(()-> new CustomException(ErrorStatus.POCKET_NOT_FOUND));

	if (!pocket.getAccount().getUser().getId().equals(requesterId)) {
	  throw new CustomException(ErrorStatus.ACCOUNT_ACCESS_DENIED);
	}

	pocketRepository.delete(pocket);
  }
}
