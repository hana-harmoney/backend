package com.example.hanaharmonybackend.domain;

import com.example.hanaharmonybackend.payload.code.ErrorStatus;
import com.example.hanaharmonybackend.payload.exception.CustomException;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;
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
  private Long accountId;

  @Column(name = "account_num", nullable = false, unique = true)
  private String accountNum;

  @Column(name = "account_balance", nullable = false)
  private Long accountBalance;

  @CreatedDate
  @Column(name = "created_at", updatable = false, nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "is_deleted", nullable = false)
  @Setter
  private boolean deleted = false;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;

  @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
  @SQLRestriction("is_deleted = false")
  private List<Pocket> pockets = new ArrayList<>();

  // === 입/출금 메서드 ===
  public void withdraw(Long amount) {
    if (this.accountBalance < amount) {
      throw new CustomException(ErrorStatus.INSUFFICIENT_ACCOUNT_BALANCE);
    }
    this.accountBalance -= amount;
  }

  public void deposit(Long amount) {
    this.accountBalance += amount;
  }
}
