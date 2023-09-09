package pl.popravko.transactionstorage.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.popravko.transactionstorage.dto.TransactionDto;
import pl.popravko.transactionstorage.dto.TransactionFilter;
import pl.popravko.transactionstorage.mapper.TransactionMapper;
import pl.popravko.transactionstorage.repository.TransactionRepository;
import pl.popravko.transactionstorage.util.Predicate;

import java.util.Map;
import java.util.Optional;

import static pl.popravko.transactionstorage.entity.QTransactionEntity.transactionEntity;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository repository;
    private final TransactionMapper mapper;

    @Transactional
    public Page<TransactionDto> findAll(TransactionFilter filter, Pageable page) {
        return repository.findAll(
                        Predicate.builder()
                                .add(filter.getAfter(), transactionEntity.timestamp::after)
                                .add(filter.getBefore(), transactionEntity.timestamp::before)
                                .add(filter.getType(), transactionEntity.type::eq)
                                .add(filter.getActor(), transactionEntity.actor::containsIgnoreCase)
                                .add(filter.getDataContains(), (map) -> {
                                    BooleanExpression exp = Expressions.TRUE;
                                    for (Map.Entry<String, String> entry : map.entrySet()) {
                                        exp = exp.and(transactionEntity.data.contains(entry.getKey(), entry.getValue()));
                                    }
                                    return exp;
                                })
                                .build(),
                        page)
                .map(mapper::toDto);
    }

    @Transactional
    public Optional<TransactionDto> find(Integer id) {
        return repository.findById(id)
                .map(mapper::toDto);
    }

    @Transactional
    public TransactionDto put(TransactionDto create) {
        return mapper.toDto(repository
                .save(mapper.toEntity(create)));
    }

    @Transactional
    public Optional<Integer> delete(Integer id) {
        return repository.findById(id)
                .map(it -> {
                    repository.deleteById(id);
                    return it.getId();
                });
    }
}
