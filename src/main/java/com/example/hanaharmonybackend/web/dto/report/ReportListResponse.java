package com.example.hanaharmonybackend.web.dto.report;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data

@Builder
public class ReportListResponse {
    List<ReportDetailResponse> reportList;
}