package com.example.hanaharmonybackend.service;

import com.example.hanaharmonybackend.domain.User;

import java.time.LocalDateTime;

public interface ReportService {
    void saveTransferReport(User receiver, Long amount, LocalDateTime regdate);
}
