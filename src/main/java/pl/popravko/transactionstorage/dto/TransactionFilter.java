package pl.popravko.transactionstorage.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Builder
@Data
public class TransactionFilter {

    private LocalDateTime after;
    private LocalDateTime before;
    private String type;
    private String actor;
    @Builder.Default
    private Map<String, String> dataContains = new HashMap<>();
}
