package com.example.hanaharmonybackend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "report")
@Getter
@NoArgsConstructor
public class Report extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(name = "month", nullable = false)
    private LocalDate month;

    @Column(name = "receipt_count")
    private Integer receiptCount;

    @Column(name = "monthly_amount")
    private Long monthlyAmount;

    public Report(User user, LocalDate month, Integer receiptCount, Long monthlyAmount) {
        this.user = user;
        this.month = month;
        this.receiptCount = receiptCount;
        this.monthlyAmount = monthlyAmount;
    }

    public void addReceipt(Long amount) {
        this.receiptCount = (this.receiptCount == 0 ? 1 : this.receiptCount + 1);
        this.monthlyAmount = (this.monthlyAmount == 0 ? amount : this.monthlyAmount + amount);
    }
}