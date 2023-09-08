package pl.popravko.transactionstorage.mapper;

import org.springframework.stereotype.Component;
import pl.popravko.transactionstorage.dto.TransactionDto;
import pl.popravko.transactionstorage.entity.TransactionEntity;

@Component
public class TransactionMapper {

    public TransactionDto toDto(TransactionEntity entity) {
        return TransactionDto.builder()
                .id(entity.getId())
                .timestamp(entity.getTimestamp())
                .type(entity.getType())
                .actor(entity.getActor())
                .data(entity.getData())
                .build();
    }

    public TransactionEntity toEntity(TransactionDto dto) {
        return TransactionEntity.builder()
                .id(dto.id())
                .timestamp(dto.timestamp())
                .type(dto.type())
                .actor(dto.actor())
                .data(dto.data())
                .build();
    }
}
