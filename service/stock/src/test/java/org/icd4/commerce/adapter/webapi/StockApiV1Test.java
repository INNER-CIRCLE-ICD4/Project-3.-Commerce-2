package org.icd4.commerce.adapter.webapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.icd4.commerce.adapter.webapi.dto.StockUpdateRequest;
import org.icd4.commerce.application.StockService;
import org.icd4.commerce.domain.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
class StockApiV1Test {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private StockService stockService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("increaseStockV1 - 성공")
    void increaseStockV1_Success() throws Exception {
        // Given
        Stock stock = stockService.register("test-product", 100L);
        String stockId = stock.getId();
        StockUpdateRequest request = new StockUpdateRequest(50L);

        // When & Then
        mockMvc.perform(patch("/api/stocks/v1/{stockId}/increase", stockId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("재고가 성공적으로 증가되었습니다."))
                .andExpect(jsonPath("$.data").value(1));

        // 재고가 실제로 증가했는지 확인
        assertThat(stockService.checkQuantity(stockId)).isEqualTo(150L);
    }

    @Test
    @DisplayName("increaseStockV1 - 존재하지 않는 재고 ID")
    void increaseStockV1_NonExistentStockId() throws Exception {
        // Given
        String nonExistentStockId = "non-existent-stock";
        StockUpdateRequest request = new StockUpdateRequest(50L);

        // When & Then
        mockMvc.perform(patch("/api/stocks/v1/{stockId}/increase", nonExistentStockId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("재고를 찾을 수 없습니다: " + nonExistentStockId));
    }

    @Test
    @DisplayName("increaseStockV1 - 음수 수량")
    void increaseStockV1_NegativeQuantity() throws Exception {
        // Given
        Stock stock = stockService.register("test-product", 100L);
        String stockId = stock.getId();
        StockUpdateRequest request = new StockUpdateRequest(-10L);

        // When & Then
        mockMvc.perform(patch("/api/stocks/v1/{stockId}/increase", stockId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("수량은 0보다 커야 합니다."));
    }

    @Test
    @DisplayName("increaseStockV1 - null 수량")
    void increaseStockV1_NullQuantity() throws Exception {
        // Given
        Stock stock = stockService.register("test-product", 100L);
        String stockId = stock.getId();
        StockUpdateRequest request = new StockUpdateRequest(null);

        // When & Then
        mockMvc.perform(patch("/api/stocks/v1/{stockId}/increase", stockId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("수량은 필수 입니다."));
    }

    @Test
    @DisplayName("increaseStockV1 - 0 수량")
    void increaseStockV1_ZeroQuantity() throws Exception {
        // Given
        Stock stock = stockService.register("test-product", 100L);
        String stockId = stock.getId();
        StockUpdateRequest request = new StockUpdateRequest(0L);

        // When & Then
        mockMvc.perform(patch("/api/stocks/v1/{stockId}/increase", stockId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("수량은 0보다 커야 합니다."));
    }

    @Test
    @DisplayName("decreaseStockV1 - 성공")
    void decreaseStockV1_Success() throws Exception {
        // Given
        Stock stock = stockService.register("test-product", 100L);
        String stockId = stock.getId();
        StockUpdateRequest request = new StockUpdateRequest(30L);

        // When & Then
        mockMvc.perform(patch("/api/stocks/v1/{stockId}/v1/decrease", stockId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("재고가 성공적으로 감소되었습니다."))
                .andExpect(jsonPath("$.data").value(1));

        // 재고가 실제로 감소했는지 확인
        assertThat(stockService.checkQuantity(stockId)).isEqualTo(70L);
    }

    @Test
    @DisplayName("decreaseStockV1 - 존재하지 않는 재고 ID")
    void decreaseStockV1_NonExistentStockId() throws Exception {
        // Given
        String nonExistentStockId = "non-existent-stock";
        StockUpdateRequest request = new StockUpdateRequest(30L);

        // When & Then
        mockMvc.perform(patch("/api/stocks/v1/{stockId}/v1/decrease", nonExistentStockId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("재고를 찾을 수 없습니다: " + nonExistentStockId));
    }

    @Test
    @DisplayName("decreaseStockV1 - 재고 수량 부족")
    void decreaseStockV1_InsufficientQuantity() throws Exception {
        // Given
        Stock stock = stockService.register("test-product", 50L);
        String stockId = stock.getId();
        StockUpdateRequest request = new StockUpdateRequest(100L);

        // When & Then
        mockMvc.perform(patch("/api/stocks/v1/{stockId}/v1/decrease", stockId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("재고 수량이 부족합니다. stockId: " + stockId));

        // 재고가 변하지 않았는지 확인
        assertThat(stockService.checkQuantity(stockId)).isEqualTo(50L);
    }

    @Test
    @DisplayName("decreaseStockV1 - 정확히 남은 수량만큼 감소")
    void decreaseStockV1_ExactRemainingQuantity() throws Exception {
        // Given
        Stock stock = stockService.register("test-product", 50L);
        String stockId = stock.getId();
        StockUpdateRequest request = new StockUpdateRequest(50L);

        // When & Then
        mockMvc.perform(patch("/api/stocks/v1/{stockId}/v1/decrease", stockId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(1));

        // 재고가 0이 되었는지 확인
        assertThat(stockService.checkQuantity(stockId)).isEqualTo(0L);
    }

    @Test
    @DisplayName("decreaseStockV1 - 음수 수량")
    void decreaseStockV1_NegativeQuantity() throws Exception {
        // Given
        Stock stock = stockService.register("test-product", 100L);
        String stockId = stock.getId();
        StockUpdateRequest request = new StockUpdateRequest(-10L);

        // When & Then
        mockMvc.perform(patch("/api/stocks/v1/{stockId}/v1/decrease", stockId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("수량은 0보다 커야 합니다."));
    }

    @Test
    @DisplayName("decreaseStockV1 - null 수량")
    void decreaseStockV1_NullQuantity() throws Exception {
        // Given
        Stock stock = stockService.register("test-product", 100L);
        String stockId = stock.getId();
        StockUpdateRequest request = new StockUpdateRequest(null);

        // When & Then
        mockMvc.perform(patch("/api/stocks/v1/{stockId}/v1/decrease", stockId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("수량은 필수 입니다."));
    }

    @Test
    @DisplayName("decreaseStockV1 - 0 수량")
    void decreaseStockV1_ZeroQuantity() throws Exception {
        // Given
        Stock stock = stockService.register("test-product", 100L);
        String stockId = stock.getId();
        StockUpdateRequest request = new StockUpdateRequest(0L);

        // When & Then
        mockMvc.perform(patch("/api/stocks/v1/{stockId}/v1/decrease", stockId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("수량은 0보다 커야 합니다."));
    }

    @Test
    @DisplayName("V1 API 연속 실행 테스트")
    void v1ApiSequentialTest() throws Exception {
        // Given
        Stock stock = stockService.register("sequential-test-product", 100L);
        String stockId = stock.getId();

        // When & Then - 증가 후 감소
        StockUpdateRequest increaseRequest = new StockUpdateRequest(50L);
        mockMvc.perform(patch("/api/stocks/v1/{stockId}/increase", stockId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(increaseRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(1));

        assertThat(stockService.checkQuantity(stockId)).isEqualTo(150L);

        StockUpdateRequest decreaseRequest = new StockUpdateRequest(30L);
        mockMvc.perform(patch("/api/stocks/v1/{stockId}/v1/decrease", stockId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(decreaseRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(1));

        assertThat(stockService.checkQuantity(stockId)).isEqualTo(120L);
    }

    @Test
    @DisplayName("V1 API 동시성 시뮬레이션 테스트")
    void v1ApiConcurrencySimulationTest() throws Exception {
        // Given
        Stock stock1 = stockService.register("concurrent-product-1", 100L);
        Stock stock2 = stockService.register("concurrent-product-2", 200L);
        String stockId1 = stock1.getId();
        String stockId2 = stock2.getId();

        // When & Then - 여러 재고에 대해 동시에 연산 수행
        StockUpdateRequest increaseRequest = new StockUpdateRequest(50L);
        StockUpdateRequest decreaseRequest = new StockUpdateRequest(30L);

        // stock1 증가
        mockMvc.perform(patch("/api/stocks/v1/{stockId}/increase", stockId1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(increaseRequest)))
                .andExpect(status().isOk());

        // stock2 감소
        mockMvc.perform(patch("/api/stocks/v1/{stockId}/v1/decrease", stockId2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(decreaseRequest)))
                .andExpect(status().isOk());

        // 결과 확인
        assertThat(stockService.checkQuantity(stockId1)).isEqualTo(150L); // 100 + 50
        assertThat(stockService.checkQuantity(stockId2)).isEqualTo(170L); // 200 - 30
    }

    @Test
    @DisplayName("V1 API 경계값 테스트")
    void v1ApiBoundaryValueTest() throws Exception {
        // Given
        Stock stock = stockService.register("boundary-test-product", 1L);
        String stockId = stock.getId();

        // When & Then - 최소 수량에서 감소
        StockUpdateRequest decreaseRequest = new StockUpdateRequest(1L);
        mockMvc.perform(patch("/api/stocks/v1/{stockId}/v1/decrease", stockId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(decreaseRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(1));

        assertThat(stockService.checkQuantity(stockId)).isEqualTo(0L);

        // 0에서 증가
        StockUpdateRequest increaseRequest = new StockUpdateRequest(1L);
        mockMvc.perform(patch("/api/stocks/v1/{stockId}/increase", stockId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(increaseRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(1));

        assertThat(stockService.checkQuantity(stockId)).isEqualTo(1L);
    }
} 