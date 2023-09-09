package pl.popravko.transactionstorage.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

@Builder
public record TransactionDto(
        @NotNull
        Integer id,
        @NotNull
        @PastOrPresent
        LocalDateTime timestamp,
        @NotNull
        @Size(max = 50)
        String type,
        @NotNull
        @Size(max = 50)
        String actor,
        Map<String, String> data
) {
}
