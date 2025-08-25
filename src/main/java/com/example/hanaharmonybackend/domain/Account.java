package com.example.hanaharmonybackend.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    @Column(name = "account_num", nullable = false, length = 25, unique = true)
    private String accountNum;

    @Column(name = "account_balance", nullable = false)
    private Long accountBalance;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // === 비즈니스 메서드 ===
    public void withdraw(Long amount) {
        if (this.accountBalance < amount) {
            throw new IllegalStateException("잔액 부족");
        }
        this.accountBalance -= amount;
    }

    public void deposit(Long amount) {
        this.accountBalance += amount;
    }
}
