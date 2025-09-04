package com.example.hanaharmonybackend.repository.repositoryImpl;

import com.example.hanaharmonybackend.domain.QTransactionHistory;
import com.example.hanaharmonybackend.domain.TransactionHistory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.hanaharmonybackend.domain.QTransactionHistory.transactionHistory;

@Slf4j
@Repository
public class TransactionHistoryRepositoryImpl implements TransactionHistoryRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public TransactionHistoryRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<TransactionHistory> findByPocketIdDsl(Long pocketId) {
        QTransactionHistory th = QTransactionHistory.transactionHistory;

        return queryFactory
                .selectFrom(th)
                .where(th.fromPocket.pocketId.eq(pocketId)
                        .or(th.toPocket.pocketId.eq(pocketId)))
                .orderBy(th.createdAt.desc())
                .fetch();
    }

    @Override
    public List<TransactionHistory> findAccountHistoryDsl(Long accountId) {
        return queryFactory
                .selectFrom(transactionHistory)
                .leftJoin(transactionHistory.fromAccount).fetchJoin()
                .leftJoin(transactionHistory.toAccount).fetchJoin()
                .leftJoin(transactionHistory.fromPocket).fetchJoin()
                .leftJoin(transactionHistory.toPocket).fetchJoin()
                .where(
                        transactionHistory.fromAccount.accountId.eq(accountId)
                                .or(transactionHistory.toAccount.accountId.eq(accountId))
                                .or(transactionHistory.fromPocket.account.accountId.eq(accountId))
                                .or(transactionHistory.toPocket.account.accountId.eq(accountId))
                )
                .orderBy(transactionHistory.createdAt.desc())
                .fetch();
    }
}
