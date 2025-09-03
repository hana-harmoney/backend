package com.example.hanaharmonybackend.repository;

import com.example.hanaharmonybackend.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    Optional<Report> findByUserIdAndMonth(Long userId, LocalDate month);
}
