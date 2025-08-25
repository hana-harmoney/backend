package com.example.hanaharmonybackend.domain;

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
}
