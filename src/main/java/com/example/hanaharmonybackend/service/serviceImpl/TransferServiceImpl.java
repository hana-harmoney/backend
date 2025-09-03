package com.example.hanaharmonybackend.service.serviceImpl;

import com.example.hanaharmonybackend.domain.Account;
import com.example.hanaharmonybackend.domain.Pocket;
import com.example.hanaharmonybackend.domain.TransactionHistory;
import com.example.hanaharmonybackend.domain.User;
import com.example.hanaharmonybackend.payload.code.ErrorStatus;
import com.example.hanaharmonybackend.payload.exception.CustomException;
import com.example.hanaharmonybackend.repository.AccountRepository;
import com.example.hanaharmonybackend.repository.PocketRepository;
import com.example.hanaharmonybackend.repository.TransactionHistoryRepository;
import com.example.hanaharmonybackend.service.TransferService;
import com.example.hanaharmonybackend.util.SecurityUtil;
import com.example.hanaharmonybackend.web.dto.transfer.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final AccountRepository accountRepository;
    private final PocketRepository pocketRepository;
    private final TransactionHistoryRepository txRepository;

    private Account getUserAccount(User loginUser) {
        return accountRepository.findByUser_Id(loginUser.getId())
                .orElseThrow(() -> new CustomException(ErrorStatus.ACCOUNT_NOT_FOUND));
    }

    @Transactional
    @Override
    public AccountTransferResponse transferAccountToAccount(AccountTransferRequest request) {
        User loginUser = SecurityUtil.getCurrentMember(); // 현재 로그인 유저
        Account from = getUserAccount(loginUser);

        Account to = accountRepository.findByAccountNumAndUser_Name(
                        request.getToAccountNum(), request.getToAccountName())
                .orElseThrow(() -> new CustomException(ErrorStatus.ACCOUNT_NOT_FOUND));

        from.withdraw(request.getAmount());
        to.deposit(request.getAmount());

        txRepository.save(TransactionHistory.builder()
                .fromAccount(from)
                .toAccount(to)
                .amount(request.getAmount())
                .build());

        return new AccountTransferResponse(
                from.getAccountBalance(),
                to.getAccountNum(),
                to.getUser().getName(),
                request.getAmount()
        );
    }

    @Transactional
    @Override
    public PocketTransferResponse transferAccountToPocket(Long pocketId, PocketTransferRequest request) {
        User loginUser = SecurityUtil.getCurrentMember();
        Account from = getUserAccount(loginUser);

        Pocket to = pocketRepository.findById(pocketId)
                .orElseThrow(() -> new CustomException(ErrorStatus.POCKET_NOT_FOUND));

        // 주머니 소유자 검증
        if (!to.getAccount().getAccountId().equals(from.getAccountId())) {
            throw new CustomException(ErrorStatus.POCKET_ACCESS_DENIED);
        }

        from.withdraw(request.getAmount());
        to.deposit(request.getAmount());

        txRepository.save(TransactionHistory.builder()
                .fromAccount(from)
                .toPocket(to)
                .amount(request.getAmount())
                .build());

        return new PocketTransferResponse(
                from.getAccountBalance(),
                to.getCurrentAmount(),
                request.getAmount(),
                to.getTargetAmount()
        );
    }

    @Transactional
    @Override
    public PocketTransferResponse transferPocketToAccount(Long pocketId, PocketTransferRequest request) {
        User loginUser = SecurityUtil.getCurrentMember();
        Account to = getUserAccount(loginUser);

        Pocket from = pocketRepository.findById(pocketId)
                .orElseThrow(() -> new CustomException(ErrorStatus.POCKET_NOT_FOUND));

        // 주머니 소유자 검증
        if (!from.getAccount().getAccountId().equals(to.getAccountId())) {
            throw new CustomException(ErrorStatus.POCKET_ACCESS_DENIED);
        }

        from.withdraw(request.getAmount());
        to.deposit(request.getAmount());

        txRepository.save(TransactionHistory.builder()
                .fromPocket(from)
                .toAccount(to)
                .amount(request.getAmount())
                .build());

        return new PocketTransferResponse(
                to.getAccountBalance(),
                from.getCurrentAmount(),
                request.getAmount(),
                from.getTargetAmount()
        );
    }
}

