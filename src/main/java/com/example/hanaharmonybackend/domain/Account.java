package com.example.hanaharmonybackend.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "account")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Account {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long accountId;

  @Column(name = "account_num", nullable = false, length = 255)
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
}
