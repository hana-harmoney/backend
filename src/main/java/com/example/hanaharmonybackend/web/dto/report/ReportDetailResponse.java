package com.example.hanaharmonybackend.web.dto.report;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ReportDetailResponse {
    Long userId;
    LocalDate month;
    Long monthlyAmount;
    Integer monthlyCount;
}