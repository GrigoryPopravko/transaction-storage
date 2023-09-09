package pl.popravko.transactionstorage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.ListQuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import pl.popravko.transactionstorage.entity.TransactionEntity;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Integer>, ListQuerydslPredicateExecutor<TransactionEntity> {
}
