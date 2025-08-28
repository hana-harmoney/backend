package com.example.hanaharmonybackend.domain;

import com.example.hanaharmonybackend.payload.code.ErrorStatus;
import com.example.hanaharmonybackend.payload.exception.CustomException;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pocket")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Pocket extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "pocket_id")
  private Long pocketId;

  @Column(name = "pocket_name", nullable = false, length = 30)
  private String pocketName;

  @Column(name = "target_amount", nullable = false)
  private Long targetAmount;

  @Column(name = "current_amount", nullable = false)
  private Long currentAmount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "account_id", nullable = false)
  private Account account;

  // === 주머니 꺼내기/채우기 메서드 ===
  public void withdraw(Long amount) {
    if (this.currentAmount < amount) {
      throw new CustomException(ErrorStatus.INSUFFICIENT_POCKET_BALANCE);
    }
    this.currentAmount -= amount;
  }

  public void deposit(Long amount) {
    this.currentAmount += amount;
  }
}
