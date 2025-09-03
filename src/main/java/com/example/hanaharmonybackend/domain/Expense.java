package com.example.hanaharmonybackend.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long expenseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    private Long livingExpense;     // 생활비
    private Long medicalExpense;    // 의료비
    private Long leisureExpense;    // 여가비
    private Long otherExpense;      // 기타

    private Integer month;          // mm
}