package com.example.hanaharmonybackend.service.serviceImpl;

import com.example.hanaharmonybackend.domain.Report;
import com.example.hanaharmonybackend.domain.User;
import com.example.hanaharmonybackend.repository.ReportRepository;
import com.example.hanaharmonybackend.service.ReportService;
import com.example.hanaharmonybackend.util.SecurityUtil;
import com.example.hanaharmonybackend.web.dto.report.ReportDetailResponse;
import com.example.hanaharmonybackend.web.dto.report.ReportListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final ReportRepository reportRepository;

    // 통계 생성 및 누적 금액 추가
    @Transactional
    @Override
    public void saveTransferReport(User receiver, Long amount, LocalDateTime regdate) {
        LocalDate monthStart = regdate
                .withDayOfMonth(1)
                .toLocalDate();

        Report report = reportRepository.findByUserIdAndMonth(receiver.getId(), monthStart)
                .orElseGet(() -> new Report(receiver, monthStart, 0, 0L));

        // 총 금액 더하기
        report.addReceipt(amount);
        reportRepository.save(report);
    }

    // 이번달 통계 조회
    @Override
    public ReportDetailResponse getTodayMonthReport() {
        User user = SecurityUtil.getCurrentMember();
        LocalDate monthStart = LocalDate.now().withDayOfMonth(1);

        Report report = reportRepository.findByUserIdAndMonth(user.getId(), monthStart)
                .orElseGet(() -> new Report(user, monthStart, 0, 0L));

        return ReportDetailResponse.builder()
                .userId(user.getId())
                .month(report.getMonth())
                .monthlyAmount(report.getMonthlyAmount())
                .monthlyCount(report.getReceiptCount())
                .build();
    }

    // 월별 통계 리스트 조회
    @Override
    public ReportListResponse getMonthlyReport() {
        User user = SecurityUtil.getCurrentMember();

        List<Report> reports = reportRepository.findAllByUserIdOrderByMonthDesc(user.getId());

        List<ReportDetailResponse> reportList = reports.stream()
                .map(report -> ReportDetailResponse.builder()
                        .userId(user.getId())
                        .month(report.getMonth())
                        .monthlyAmount(report.getMonthlyAmount())
                        .monthlyCount(report.getReceiptCount())
                        .build())
                .collect(Collectors.toList());

        return ReportListResponse.builder()
                .reportList(reportList)
                .build();
    }

    // 과거 하모니 통계 이력 자동 생성
    @Transactional
    public void initializeReportsForNewUser(User user) {
        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();

        for (int month = 1; month < currentMonth; month++) {
            LocalDate monthStart = LocalDate.of(now.getYear(), month, 1);

            int receiptCount = ThreadLocalRandom.current().nextInt(0, 31); // 0 ~ 100
            long totalAmount = ThreadLocalRandom.current().nextLong(50_000, 1_000_001);
            totalAmount = (totalAmount / 1000) * 1000;

            Report report = new Report(
                    user,
                    monthStart,
                    receiptCount,
                    totalAmount
            );
            reportRepository.save(report);
        }
    }
}