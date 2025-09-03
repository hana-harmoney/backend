package com.example.hanaharmonybackend.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Income {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long incomeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    private Long pension;           // 연금
    private Long rentIncome;        // 임대소득
    private Long harmoneyIncome;    // 하모니
    private Long otherIncome;       // 기타

    private Integer month;          // mm
}