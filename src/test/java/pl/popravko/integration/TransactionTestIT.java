package pl.popravko.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import pl.popravko.transactionstorage.dto.TransactionDto;
import pl.popravko.transactionstorage.dto.TransactionFilter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings({"checkstyle:magicnumber"})
@TestMethodOrder(OrderAnnotation.class)
@AutoConfigureMockMvc
public class TransactionTestIT extends TransactionTestData {

    @Autowired
    private MockMvc mockMvc;

    @Order(0)
    @Test
    void testAddTransactionSuccessfully() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        for (TransactionDto createDto : TEST_TRANSACTIONS) {
            ResultActions resultActions = mockMvc.perform(post("/transaction")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createDto)));

            resultActions
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(createDto.id()));
        }
    }

    @Order(1)
    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("getArgumentsAddTransactionUnsuccessfully")
    void testAddTransactionUnsuccessfully(String name, TransactionDto createDto) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        ResultActions resultActions = mockMvc.perform(post("/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)));

        resultActions
                .andExpect(status().isBadRequest());
    }

    @Order(2)
    @Test
    void testFindById() throws Exception {
        for (TransactionDto dto : TEST_TRANSACTIONS) {
            ResultActions resultActions = mockMvc.perform(get("/transaction/{id}", dto.id())
                    .contentType(MediaType.APPLICATION_JSON));

            resultActions
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(dto.id()))
                    .andExpect(jsonPath("$.timestamp").value(dto.timestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"))))
                    .andExpect(jsonPath("$.type").value(dto.type()))
                    .andExpect(jsonPath("$.actor").value(dto.actor()))
                    .andExpect(jsonPath("$.data").value(dto.data()));
        }
    }

    @Order(3)
    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("getArgumentsTestFindAll")
    void testFindAll(String name, TransactionFilter filter, List<TransactionDto> expected) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")));
        module.addSerializer(LocalDateTime.class, LocalDateTimeSerializer.INSTANCE);
        objectMapper.registerModule(module);
        String body = mockMvc.perform(get("/transaction")
                        .params(toRequestParam(filter))
                        .param("size", "1000")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Map page = objectMapper.readValue(body, HashMap.class);
        List<TransactionDto> actual = objectMapper.convertValue(page.get("content"), new TypeReference<List<TransactionDto>>() {
        });

        assertThat(actual).hasSameElementsAs(expected);
    }

    @Order(4)
    @Test
    void testUpdate() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        TransactionDto updatable = TEST_TRANSACTIONS.get(0);
        mockMvc.perform(put("/transaction/{id}", updatable.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TransactionDto.builder()
                                .id(200)
                                .build()
                        )))
                .andExpect(status().isBadRequest());


        TransactionDto expected = TransactionDto.builder()
                .id(updatable.id())
                .timestamp(LocalDateTime.now().withMinute(0))
                .type(getRandomType())
                .actor(generateRandomActor(50))
                .data(generateRandomData())
                .build();

        ResultActions resultActions200 = mockMvc.perform(put("/transaction/{id}", updatable.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expected)));
        resultActions200
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(expected.id()))
                .andExpect(jsonPath("$.timestamp").value(expected.timestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"))))
                .andExpect(jsonPath("$.type").value(expected.type()))
                .andExpect(jsonPath("$.actor").value(expected.actor()))
                .andExpect(jsonPath("$.data").value(expected.data()));
    }

    @Order(5)
    @Test
    void testDelete() throws Exception {
        for (TransactionDto dto : TEST_TRANSACTIONS) {
            mockMvc.perform(delete("/transaction/{id}", dto.id())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            mockMvc.perform(get("/transaction/{id}", dto.id())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

        }
    }
}
