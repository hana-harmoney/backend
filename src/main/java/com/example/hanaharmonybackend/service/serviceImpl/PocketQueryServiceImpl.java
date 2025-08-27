package com.example.hanaharmonybackend.service.serviceImpl;

import com.example.hanaharmonybackend.domain.Pocket;
import com.example.hanaharmonybackend.domain.TransactionHistory;
import com.example.hanaharmonybackend.payload.code.ErrorStatus;
import com.example.hanaharmonybackend.payload.exception.CustomException;
import com.example.hanaharmonybackend.repository.PocketRepository;
import com.example.hanaharmonybackend.repository.TransactionHistoryRepository;
import com.example.hanaharmonybackend.service.PocketQueryService;
import com.example.hanaharmonybackend.web.dto.TransactionType;
import com.example.hanaharmonybackend.web.dto.pocket.PocketDetailResponse;
import com.example.hanaharmonybackend.web.dto.pocket.PocketTxDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PocketQueryServiceImpl implements PocketQueryService {

  private final PocketRepository pocketRepository;
  private final TransactionHistoryRepository txRepository;

  @Override
  public PocketDetailResponse getDetail(Long pocketId, Long requesterId) {
	Pocket pocket = pocketRepository.findById(pocketId)
		.orElseThrow(() -> new CustomException(ErrorStatus.POCKET_NOT_FOUND));

	if (!pocket.getAccount().getUser().getId().equals(requesterId)) {
	  throw new CustomException(ErrorStatus.POCKET_ACCESS_DENIED);
	}

	List<TransactionHistory> rows =
		txRepository.findByPocketId(pocketId);

	List<PocketTxDto> transactions = rows.stream().map(tx -> {
	  TransactionType dir =
		  (tx.getFromPocket() != null && pocketId.equals(tx.getFromPocket().getPocketId()))
			  ? TransactionType.OUT
			  : TransactionType.IN;

	  String fromName =
		  tx.getFromPocket()  != null ? tx.getFromPocket().getPocketName()
			  : tx.getFromAccount() != null ? tx.getFromAccount().getAccountNum()
			  : null;

	  String toName =
		  tx.getToPocket()  != null ? tx.getToPocket().getPocketName()
			  : tx.getToAccount() != null ? tx.getToAccount().getAccountNum()
			  : null;

	  return PocketTxDto.builder()
		  .txId(tx.getId())
		  .direction(dir)
		  .amount(tx.getAmount())
		  .createdAt(tx.getCreatedAt())
		  .fromPocketId(tx.getFromPocket() != null ? tx.getFromPocket().getPocketId() : null)
		  .toPocketId(tx.getToPocket() != null ? tx.getToPocket().getPocketId() : null)
		  .fromAccountId(tx.getFromAccount() != null ? tx.getFromAccount().getAccountId() : null)
		  .toAccountId(tx.getToAccount() != null ? tx.getToAccount().getAccountId() : null)
		  .fromName(fromName)
		  .toName(toName)
		  .build();
	}).toList();

	return PocketDetailResponse.builder()
		.pocketId(pocket.getPocketId())
		.pocketName(pocket.getPocketName())
		.targetAmount(pocket.getTargetAmount())
		.currentAmount(pocket.getCurrentAmount())
		.transactions(transactions)
		.build();
  }
}
