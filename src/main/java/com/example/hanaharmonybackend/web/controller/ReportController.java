package com.example.hanaharmonybackend.web.controller;

import com.example.hanaharmonybackend.payload.ApiResponse;
import com.example.hanaharmonybackend.service.ReportService;
import com.example.hanaharmonybackend.web.dto.report.ReportDetailResponse;
import com.example.hanaharmonybackend.web.dto.report.ReportListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Report", description = "리포트 API")
@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @Operation(summary = "이번달 하모니 수익 조회", description = "유저의 이번달 하모니 수익을 조회합니다.(오늘 날짜 기준)")
    @GetMapping("")
    public ApiResponse<ReportDetailResponse> getTodayMonthReport() {
        return ApiResponse.success(reportService.getTodayMonthReport());
    }

    @Operation(summary = "월별 하모니 수익 조회", description = "유저의 월별 하모니 수익을 조회합니다.")
    @GetMapping("/list")
    public ApiResponse<ReportListResponse> getMonthlyReport() {
        return ApiResponse.success(reportService.getMonthlyReport());
    }
}