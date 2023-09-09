package pl.popravko.integration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.util.LinkedMultiValueMap;
import pl.popravko.transactionstorage.dto.TransactionDto;
import pl.popravko.transactionstorage.dto.TransactionFilter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

@SuppressWarnings({"checkstyle:magicnumber"})
public abstract class TransactionTestData extends IntegrationTestBase {

    private static final Random RANDOM = new Random();
    static final List<TransactionDto> TEST_TRANSACTIONS = new ArrayList<>();

    @BeforeAll
    static void initData() {
        generateTransactionDtos();
    }

    private static void generateTransactionDtos() {
        for (int i = 0; i < 1000; i++) {
            TransactionDto dto = TransactionDto.builder()
                    .id(i)
                    .timestamp(LocalDateTime.now().withMinute(0).withSecond(0).withNano(0).minusHours(RANDOM.nextInt(24)))
                    .type(getRandomType())
                    .actor(generateRandomActor(50))
                    .data(generateRandomData())
                    .build();

            TEST_TRANSACTIONS.add(dto);
        }
    }

    static String getRandomType() {
        String[] strings = {"Type1", "Type2", "Type3"};
        return strings[RANDOM.nextInt(strings.length)];
    }

    static String generateRandomActor(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(RANDOM.nextInt(characters.length())));
        }
        return sb.toString();
    }

    static Map<String, String> generateRandomData() {
        Map<String, String> data = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            data.put(getRandomType(), getRandomType());
        }
        return data;
    }

    static LinkedMultiValueMap<String, String> toRequestParam(TransactionFilter filter) {
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("after", filter.getAfter() == null ? null : filter.getAfter().toString());
        params.add("before", filter.getBefore() == null ? null : filter.getBefore().toString());
        params.add("type", filter.getType());
        params.add("actor", filter.getActor());
        filter.getDataContains().forEach((k, v) -> {
            params.add("dataContains[%s]".formatted(k), v);
        });

        return params;
    }

    public static Stream<Arguments> getArgumentsAddTransactionUnsuccessfully() {
        return Stream.of(
                Arguments.of(
                        "Id is empty",
                        TransactionDto.builder()
                                .timestamp(LocalDateTime.now().withMinute(0))
                                .type(generateRandomActor(50))
                                .actor(generateRandomActor(50))
                                .data(generateRandomData())
                                .build()
                ),
                Arguments.of(
                        "Timestamp is empty",
                        TransactionDto.builder()
                                .id(RANDOM.nextInt(1000))
                                .type(generateRandomActor(50))
                                .actor(generateRandomActor(50))
                                .data(generateRandomData())
                                .build()
                ),
                Arguments.of(
                        "Type is empty",
                        TransactionDto.builder()
                                .id(RANDOM.nextInt(1000))
                                .timestamp(LocalDateTime.now())
                                .actor(generateRandomActor(50))
                                .data(generateRandomData())
                                .build()
                ),
                Arguments.of(
                        "Actor is empty",
                        TransactionDto.builder()
                                .id(RANDOM.nextInt(1000))
                                .timestamp(LocalDateTime.now())
                                .type(generateRandomActor(50))
                                .data(generateRandomData())
                                .build()
                ),
                Arguments.of(
                        "Timestamp in future",
                        TransactionDto.builder()
                                .id(RANDOM.nextInt(1000))
                                .timestamp(LocalDateTime.now().plusMinutes(1))
                                .type(generateRandomActor(50))
                                .actor(generateRandomActor(50))
                                .data(generateRandomData())
                                .build()
                ),
                Arguments.of(
                        "Type length more than 50",
                        TransactionDto.builder()
                                .id(RANDOM.nextInt(1000))
                                .timestamp(LocalDateTime.now())
                                .type(generateRandomActor(51))
                                .actor(generateRandomActor(50))
                                .data(generateRandomData())
                                .build()
                ),
                Arguments.of(
                        "Actor length more than 50",
                        TransactionDto.builder()
                                .id(RANDOM.nextInt(1000))
                                .timestamp(LocalDateTime.now())
                                .type(generateRandomActor(50))
                                .actor(generateRandomActor(51))
                                .data(generateRandomData())
                                .build()
                ));
    }

    public static Stream<Arguments> getArgumentsTestFindAll() {
        return Stream.of(
                Arguments.of(
                        "Empty filter",
                        TransactionFilter.builder().build(),
                        TEST_TRANSACTIONS
                ),
                Arguments.of(
                        "Timestamp before",
                        TransactionFilter.builder()
                                .before(LocalDateTime.now().minusHours(6))
                                .build(),
                        TEST_TRANSACTIONS.stream()
                                .filter(it -> it.timestamp().isBefore(LocalDateTime.now().minusHours(6)))
                                .toList()
                ),
                Arguments.of(
                        "Timestamp after",
                        TransactionFilter.builder()
                                .after(LocalDateTime.now().minusHours(18))
                                .build(),
                        TEST_TRANSACTIONS.stream()
                                .filter(it -> it.timestamp().isAfter(LocalDateTime.now().minusHours(18)))
                                .toList()
                ),
                Arguments.of(
                        "Timestamp between",
                        TransactionFilter.builder()
                                .before(LocalDateTime.now().minusHours(6))
                                .after(LocalDateTime.now().minusHours(18))
                                .build(),
                        TEST_TRANSACTIONS.stream()
                                .filter(it -> it.timestamp().isBefore(LocalDateTime.now().minusHours(6))
                                        && it.timestamp().isAfter(LocalDateTime.now().minusHours(18)))
                                .toList()
                ),
                Arguments.of(
                        "Type is",
                        TransactionFilter.builder()
                                .type("Type1")
                                .build(),
                        TEST_TRANSACTIONS.stream()
                                .filter(it -> it.type().equals("Type1"))
                                .toList()
                ),
                Arguments.of(
                        "Actor like",
                        TransactionFilter.builder()
                                .actor(TEST_TRANSACTIONS.get(5).actor().substring(0, 2))
                                .build(),
                        TEST_TRANSACTIONS.stream()
                                .filter(it -> it.actor().toLowerCase().contains(TEST_TRANSACTIONS.get(5).actor().substring(0, 2).toLowerCase()))
                                .toList()
                ),
                Arguments.of(
                        "Data contains",
                        TransactionFilter.builder()
                                .dataContains(Map.of("Type1", "Type1", "Type2", "Type2"))
                                .build(),
                        TEST_TRANSACTIONS.stream()
                                .filter(it -> "Type1".equals(it.data().get("Type1")))
                                .filter(it -> "Type2".equals(it.data().get("Type2")))
                                .toList()

                ),
                Arguments.of(
                        "Timestamp between & Data contains",
                        TransactionFilter.builder()
                                .before(LocalDateTime.now().minusHours(6))
                                .after(LocalDateTime.now().minusHours(18))
                                .dataContains(Map.of("Type1", "Type1", "Type2", "Type2"))
                                .build(),
                        TEST_TRANSACTIONS.stream()
                                .filter(it -> it.timestamp().isBefore(LocalDateTime.now().minusHours(6))
                                        && it.timestamp().isAfter(LocalDateTime.now().minusHours(18)))
                                .filter(it -> "Type1".equals(it.data().get("Type1")))
                                .filter(it -> "Type2".equals(it.data().get("Type2")))
                                .toList()
                ),
                Arguments.of(
                        "Timestamp between & Type is",
                        TransactionFilter.builder()
                                .before(LocalDateTime.now().minusHours(6))
                                .after(LocalDateTime.now().minusHours(18))
                                .type("Type2")
                                .build(),
                        TEST_TRANSACTIONS.stream()
                                .filter(it -> it.timestamp().isBefore(LocalDateTime.now().minusHours(6))
                                        && it.timestamp().isAfter(LocalDateTime.now().minusHours(18)))
                                .filter(it -> it.type().equals("Type2"))
                                .toList()
                ),
                Arguments.of(
                        "Timestamp between & Type is & Data contains",
                        TransactionFilter.builder()
                                .before(LocalDateTime.now().minusHours(6))
                                .after(LocalDateTime.now().minusHours(18))
                                .type("Type2")
                                .dataContains(Map.of("Type1", "Type1", "Type2", "Type2"))
                                .build(),
                        TEST_TRANSACTIONS.stream()
                                .filter(it -> it.timestamp().isBefore(LocalDateTime.now().minusHours(6))
                                        && it.timestamp().isAfter(LocalDateTime.now().minusHours(18)))
                                .filter(it -> it.type().equals("Type2"))
                                .filter(it -> "Type1".equals(it.data().get("Type1")))
                                .filter(it -> "Type2".equals(it.data().get("Type2")))
                                .toList()
                )
        );
    }
}
