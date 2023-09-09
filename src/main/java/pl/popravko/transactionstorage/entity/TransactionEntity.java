package pl.popravko.transactionstorage.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static jakarta.persistence.FetchType.EAGER;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transaction")
public class TransactionEntity {

    @Id
    private Integer id;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @Column(name = "actor", nullable = false, length = 50)
    private String actor;

    @Builder.Default
    @ElementCollection(fetch = EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @CollectionTable(name = "transaction_data", joinColumns = @JoinColumn(name = "transaction_id"))
    @MapKeyColumn(name = "tag", length = 50)
    @Column(name = "value", length = 50)
    private Map<String, String> data = new HashMap<>();
}
