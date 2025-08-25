package com.example.hanaharmonybackend.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "account")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

  @Column(name = "account_num", nullable = false, unique = true, length = 255)
  private String accountNum;

  @Column(name = "account_balance", nullable = false)
  private Long accountBalance;

  @CreatedDate
  @Column(name = "created_at", updatable = false, nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "is_deleted", nullable = false)
  private boolean deleted = false;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;

  @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Pocket> pockets = new ArrayList<>();
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
