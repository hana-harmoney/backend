package com.example.hanaharmonybackend.service.serviceImpl;

import com.example.hanaharmonybackend.domain.*;
import com.example.hanaharmonybackend.domain.enumerate.GENDER;
import com.example.hanaharmonybackend.payload.code.ErrorStatus;
import com.example.hanaharmonybackend.payload.exception.CustomException;
import com.example.hanaharmonybackend.repository.AccountRepository;
import com.example.hanaharmonybackend.repository.PocketRepository;
import com.example.hanaharmonybackend.repository.TransactionHistoryRepository;
import com.example.hanaharmonybackend.util.SecurityUtil;
import com.example.hanaharmonybackend.web.dto.transfer.AccountTransferRequest;
import com.example.hanaharmonybackend.web.dto.transfer.PocketTransferRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceImplTest {

    // --- Mock 객체들 ---
    // Repository 들은 DB 접근 대신 Mockito가 가짜 객체를 생성해줌
    @Mock AccountRepository accountRepository;
    @Mock PocketRepository pocketRepository;
    @Mock TransactionHistoryRepository txRepository;

    // @InjectMocks → 위 Mock들을 주입받아서 실제 서비스 로직만 테스트
    @InjectMocks TransferServiceImpl service;

    // 테스트용 사용자 / 계좌 / 주머니
    User me;
    Account myAccount;
    Pocket myPocket;
    User other;
    Account otherAccount;

    // ===== helpers =====
    // User / Account / Pocket 을 빠르게 만들어주는 헬퍼 메서드
    private User user(Long id, String loginId, String name) {
        return User.builder()
                .id(id)
                .loginId(loginId)
                .password("encoded")
                .name(name)
                .birth("1999-01-01")
                .gender(GENDER.MALE)
                .phone("010-0000-0000")
                .address("서울시")
                .latitude(37.5)
                .longitude(127.0)
                .deleted(false)
                .build();
    }

    private Account account(Long id, User owner, String accNum, long balance) {
        Account acc = Account.builder()
                .accountId(id)
                .accountNum(accNum)
                .accountBalance(balance)
                .user(owner)
                .deleted(false)
                .build();
        owner.setAccount(acc); // user ↔ account 연관관계 세팅
        return acc;
    }

    private Pocket pocket(Long id, Account acc, String name, long current, long target) {
        return Pocket.builder()
                .pocketId(id)
                .pocketName(name)
                .currentAmount(current)
                .targetAmount(target)
                .account(acc)
                .deleted(false)
                .build();
    }

    // 각 테스트 실행 전마다 공통 데이터 세팅
    @BeforeEach
    void setUp() {
        me = user(1L, "me123", "나");
        myAccount = account(100L, me, "111-222", 10_000L);
        myPocket = pocket(200L, myAccount, "여행", 1_000L, 5_000L);

        other = user(2L, "other123", "상대");
        otherAccount = account(101L, other, "333-444", 2_000L);
    }

    @Test
    @DisplayName("계좌 간 송금 성공 시 잔액과 거래내역이 정상 반영된다")
    void 계좌간송금_성공() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            // 현재 로그인 사용자 지정
            mocked.when(SecurityUtil::getCurrentMember).thenReturn(me);

            // 내 계좌 / 상대 계좌 Mock 세팅
            when(accountRepository.findByUser_Id(me.getId())).thenReturn(Optional.of(myAccount));
            when(accountRepository.findByAccountNumAndUser_Name("333-444", "상대"))
                    .thenReturn(Optional.of(otherAccount));

            // 송금 요청 (3,000원)
            AccountTransferRequest req = new AccountTransferRequest("333-444", "상대", 3_000L);

            var res = service.transferAccountToAccount(req);

            // --- 결과 검증 ---
            assertThat(myAccount.getAccountBalance()).isEqualTo(7_000L); // 10000 -> 7000
            assertThat(otherAccount.getAccountBalance()).isEqualTo(5_000L); // 2000 -> 5000
            assertThat(res.getAmount()).isEqualTo(3_000L);

            // 거래내역 저장이 호출됐는지 확인
            verify(txRepository).save(any(TransactionHistory.class));
        }
    }

    @Test
    @DisplayName("수신 계좌가 없을 경우 ACCOUNT_NOT_FOUND 예외를 던진다")
    void 계좌간송금_수신계좌없음() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::getCurrentMember).thenReturn(me);

            when(accountRepository.findByUser_Id(me.getId())).thenReturn(Optional.of(myAccount));
            when(accountRepository.findByAccountNumAndUser_Name("999-000", "모름"))
                    .thenReturn(Optional.empty()); // 상대 계좌 없음

            AccountTransferRequest req = new AccountTransferRequest("999-000", "모름", 1_000L);

            // CustomException + 상태코드 확인
            assertThatThrownBy(() -> service.transferAccountToAccount(req))
                    .isInstanceOfSatisfying(CustomException.class, ex -> {
                        assertThat(ex.getStatus()).isEqualTo(ErrorStatus.ACCOUNT_NOT_FOUND);
                        assertThat(ex.getErrorReason().getMessage())
                                .isEqualTo(ErrorStatus.ACCOUNT_NOT_FOUND.getMessage());
                    });
        }
    }

    @Test
    @DisplayName("계좌에서 주머니로 송금 성공 시 잔액과 주머니 금액이 정상 반영된다")
    void 계좌에서주머니로_성공() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            // 현재 로그인 사용자 지정
            mocked.when(SecurityUtil::getCurrentMember).thenReturn(me);

            when(accountRepository.findByUser_Id(me.getId())).thenReturn(Optional.of(myAccount));
            when(pocketRepository.findById(myPocket.getPocketId())).thenReturn(Optional.of(myPocket));

            PocketTransferRequest req = new PocketTransferRequest(2_000L);

            var res = service.transferAccountToPocket(myPocket.getPocketId(), req);

            // --- 결과 검증 ---
            assertThat(res.getChange()).isEqualTo(8_000L);        // 계좌 잔액 (10000 -> 8000)
            assertThat(res.getPocketAmount()).isEqualTo(3_000L);  // 주머니 잔액 (1000 -> 3000)
            assertThat(res.getAmount()).isEqualTo(2_000L);        // 이체 금액
            assertThat(res.getTargetAmount()).isEqualTo(5_000L);  // 목표 금액
        }
    }

    @Test
    @DisplayName("주머니에서 계좌로 송금 성공 시 잔액과 주머니 금액이 정상 반영된다")
    void 주머니에서계좌로_성공() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::getCurrentMember).thenReturn(me);

            when(accountRepository.findByUser_Id(me.getId())).thenReturn(Optional.of(myAccount));
            when(pocketRepository.findById(myPocket.getPocketId())).thenReturn(Optional.of(myPocket));

            PocketTransferRequest req = new PocketTransferRequest(500L);

            var res = service.transferPocketToAccount(myPocket.getPocketId(), req);

            assertThat(res.getChange()).isEqualTo(10_500L);       // 계좌 잔액 (10000 -> 10500)
            assertThat(res.getPocketAmount()).isEqualTo(500L);    // 주머니 잔액 (1000 -> 500)
            assertThat(res.getAmount()).isEqualTo(500L);          // 이체 금액
            assertThat(res.getTargetAmount()).isEqualTo(5_000L);  // 목표 금액
        }
    }

    @Test
    @DisplayName("내 계좌에서 내 소유가 아닌 주머니로 송금 시 POCKET_ACCESS_DENIED 예외를 던진다")
    void 계좌에서주머니로_소유권오류() {
        // 상대방의 계좌 + 주머니 생성
        Account otherAcc = account(999L, other, "555-666", 3_000L);
        Pocket otherPocket = pocket(300L, otherAcc, "상대주머니", 500L, 1_000L);

        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::getCurrentMember).thenReturn(me);

            when(accountRepository.findByUser_Id(me.getId())).thenReturn(Optional.of(myAccount));
            when(pocketRepository.findById(otherPocket.getPocketId())).thenReturn(Optional.of(otherPocket));

            PocketTransferRequest req = new PocketTransferRequest(100L);

            // --- 예외 검증 ---
            assertThatThrownBy(() -> service.transferAccountToPocket(otherPocket.getPocketId(), req))
                    .isInstanceOfSatisfying(CustomException.class, ex -> {
                        assertThat(ex.getStatus()).isEqualTo(ErrorStatus.POCKET_ACCESS_DENIED);
                        assertThat(ex.getErrorReason().getMessage())
                                .isEqualTo(ErrorStatus.POCKET_ACCESS_DENIED.getMessage());
                    });
        }
    }

    @DisplayName("계좌 간 송금 시 잔액 부족하면 INSUFFICIENT_ACCOUNT_BALANCE 예외를 던진다")
    @Test
    void 계좌간송금_잔액부족() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::getCurrentMember).thenReturn(me);

            when(accountRepository.findByUser_Id(me.getId())).thenReturn(Optional.of(myAccount));
            when(accountRepository.findByAccountNumAndUser_Name("333-444", "상대"))
                    .thenReturn(Optional.of(otherAccount));

            // 내 계좌 잔액은 10,000L → 송금 요청 50,000L → 부족
            AccountTransferRequest req = new AccountTransferRequest("333-444", "상대", 50_000L);

            assertThatThrownBy(() -> service.transferAccountToAccount(req))
                    .isInstanceOfSatisfying(CustomException.class, ex -> {
                        assertThat(ex.getStatus()).isEqualTo(ErrorStatus.INSUFFICIENT_ACCOUNT_BALANCE);
                        assertThat(ex.getErrorReason().getMessage())
                                .isEqualTo(ErrorStatus.INSUFFICIENT_ACCOUNT_BALANCE.getMessage());
                    });
        }
    }
}
