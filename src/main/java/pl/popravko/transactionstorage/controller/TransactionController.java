package pl.popravko.transactionstorage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.popravko.transactionstorage.dto.TransactionDto;
import pl.popravko.transactionstorage.dto.TransactionFilter;
import pl.popravko.transactionstorage.service.TransactionService;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;
import static pl.popravko.transactionstorage.util.UrlPath.TRANSACTION;

@RestController
@RequestMapping(TRANSACTION)
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<Page<TransactionDto>> findAll(TransactionFilter filter, @PageableDefault(size = 50) Pageable page) {
        return ok(transactionService.findAll(filter, page));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDto> find(@PathVariable Integer id) {
        return transactionService.find(id)
                .map(ResponseEntity::ok)
                .orElseGet(notFound()::build);
    }

    @PostMapping
    public ResponseEntity<TransactionDto> create(@RequestBody @Validated TransactionDto create) {
        return ok(transactionService.put(create));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionDto> update(@PathVariable Integer id, @RequestBody @Validated TransactionDto update) {
        return id == update.id()
                ? ok(transactionService.put(update))
                : badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Integer> delete(@PathVariable Integer id) {
        return transactionService.delete(id)
                .map(ResponseEntity::ok)
                .orElseGet(notFound()::build);
    }
}
