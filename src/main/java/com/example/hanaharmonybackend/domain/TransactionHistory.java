package com.example.hanaharmonybackend.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(
	name = "transaction_history",
	indexes = {
		@Index(name = "ix_tx_from_account", columnList = "from_account_id"),
		@Index(name = "ix_tx_to_account",   columnList = "to_account_id"),
		@Index(name = "ix_tx_from_pocket",  columnList = "from_pocket_id"),
		@Index(name = "ix_tx_to_pocket",    columnList = "to_pocket_id"),
		@Index(name = "ix_tx_created_at",   columnList = "created_at")
	}
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class TransactionHistory extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "tx_id")
  private Long id;

  // 송금 계좌 (NULL 가능)
  @ManyToOne(fetch = FetchType.LAZY, optional = true)
  @JoinColumn(name = "from_account_id", referencedColumnName = "accountId")
  private Account fromAccount;

  // 수취 계좌 (NULL 가능)
  @ManyToOne(fetch = FetchType.LAZY, optional = true)
  @JoinColumn(name = "to_account_id", referencedColumnName = "accountId")
  private Account toAccount;

  // 출금 주머니 (NULL 가능)
  @ManyToOne(fetch = FetchType.LAZY, optional = true)
  @JoinColumn(name = "from_pocket_id", referencedColumnName = "pocket_id")
  private Pocket fromPocket;

  // 입금 주머니 (NULL 가능)
  @ManyToOne(fetch = FetchType.LAZY, optional = true)
  @JoinColumn(name = "to_pocket_id", referencedColumnName = "pocket_id")
  private Pocket toPocket;

  // 거래 금액 (NOT NULL)
  @Column(name = "amount", nullable = false)
  private Long amount;

}
