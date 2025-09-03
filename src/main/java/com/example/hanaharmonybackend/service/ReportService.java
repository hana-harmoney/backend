package com.example.hanaharmonybackend.service;

import com.example.hanaharmonybackend.domain.User;
import com.example.hanaharmonybackend.web.dto.report.ReportDetailResponse;
import com.example.hanaharmonybackend.web.dto.report.ReportListResponse;

import java.time.LocalDateTime;

public interface ReportService {
    void saveTransferReport(User receiver, Long amount, LocalDateTime regdate);

    ReportDetailResponse getTodayMonthReport();

    ReportListResponse getMonthlyReport();
}
