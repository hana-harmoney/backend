package com.example.hanaharmonybackend.service.serviceImpl;

import com.example.hanaharmonybackend.domain.Report;
import com.example.hanaharmonybackend.domain.User;
import com.example.hanaharmonybackend.repository.ReportRepository;
import com.example.hanaharmonybackend.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final ReportRepository reportRepository;

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
}